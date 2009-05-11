
;; <copyleft>

;; Copyright 2008-2009 Dan Reish

;; This program is free software; you can redistribute it and/or
;; modify it under the terms of the GNU General Public License as
;; published by the Free Software Foundation; either version 3 of the
;; License, or (at your option) any later version.

;; This program is distributed in the hope that it will be useful, but
;; WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
;; General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program; if not, see <http://www.gnu.org/licenses>.

;; Additional permission under GNU GPL version 3 section 7

;; If you modify this Program, or any covered work, by linking or
;; combining it with clojure-contrib (or a modified version of that
;; library), containing parts covered by the terms of the Eclipse
;; Public License, the licensors of this Program grant you additional
;; permission to convey the resulting work. {Corresponding Source for
;; a non-source form of such a combination shall include the source
;; code for the parts of clojure-contrib used as well as that of the
;; covered work.}

;; </copyleft>

(ns bigkey-dm
  (:use clojure.contrib.def
        durable-maps
        util))

; Takes a name and a spec defining how a pseudo-key column (which may
; be text or blob) is to be transformed to something fixed-length, and
; returns a durable-map-style interface on that (which participates in
; durable-map transactions).  Unlike durable-maps, a row hash need not
; follow any particular column structure, and key names need not be
; valid SQL column names.  Each row is any arbitrary printable and
; readable Clojure hash-map.
;
; Reserves the use of all durable-maps with names beginning
; "bigkey-dm/".

(defvar- get-dmap :dmap)
(defvar- get-key-munger :key-munger-fn)
(defvar- get-abstract-keycol #(-> % :spec :abstract-keycol))

(defvar- *bk-maps* (atom nil))

(defn bk-startup!
  "Loads the map of bk-maps.  Must be called after dm-startup!."
  []
  (io!
   (or (compare-and-set! *bk-maps*
                         nil
                         (dm-get-map "bigkey-dm/mbk-maps"))
       (throw (Exception. "Database connection already established.")))))

(defn bk-init!
  "Initializes the permanent data structures used by bigkey-dm.
  Compile and run this function separately before using bigkey-dm.
  Like dm-init!, it can be run more than once, but it may not be
  called from inside a transaction."
  []
  (when-not (dm-get-map "bigkey-dm/mbk-maps")
    (dm-create-map! "bigkey-dm/mbk-maps"
                    {:cols {:exname ["VARCHAR(256)" :str]
                            :spec ["TEXT" :obj]}
                     :key :exname})))

(defn bk-select
  [bk k]
  (let [munged-key ((get-key-munger bk) (pr-str k))]
    (if-let [dm-row ((get-dmap bk) munged-key)]
        (let [valcol (dm-row :val_hash)]
          (valcol k)))))

(defn- close-bk
  [bk]
  (fn
    ([] bk)
    ([k] (bk-select bk k))))

(defn- name-transform
  [name]
  (str "bigkey-dm/u" name))

(defn bk-insert
  "Add a new map entry, throwing an exception if an entry with the
  given key already exists.  Returns bkc."
  [bkc row]
  (let [bk (bkc)
        dmap (get-dmap bk)
        abstract-keycol (get-abstract-keycol bk)
        k (row abstract-keycol)
        munged-key ((get-key-munger bk) (pr-str k))]
    (if-let [dm-row (dmap munged-key)]
        (let [dm-row-val (dm-row :val_hash)]
          (if (dm-row-val k)
            (throw (Exception. "Insert with already-existing key"))
            (dm-update dmap munged-key
                       assoc-in [:val_hash k] row)))
      (dm-insert dmap {:hashed_key munged-key,
                       :val_hash {k row}}))
    bkc))

(defn bk-update
  [bkc keyval f & args]
  (let [bk (bkc)
        dmap (get-dmap bk)
        abstract-keycol (get-abstract-keycol bk)
        munged-key ((get-key-munger bk) (pr-str keyval))
        ; Ultimate GOTO:
        nonexist-err #(throw (Exception. "Update on non-existent row"))]
    (if-let [dm-row (dmap munged-key)]
        (if-let [bk-row ((dm-row :val_hash) keyval)]
            (let [new-bk-row (apply f bk-row args)]
              (dm-update dmap munged-key
                         assoc-in [:val_hash keyval] new-bk-row)
              new-bk-row)
          (nonexist-err))
      (nonexist-err))))

(defvar- mem-eval
  (memoize
   (fn [form]
     (eval form))))

(defvar bk-get-map
  (memoize
   (fn
     [name]
     (if-let [bkm (@*bk-maps* name)]
         (close-bk (assoc bkm
                     :key-munger-fn (mem-eval
                                     (-> bkm :spec :key-munger))
                     :dmap (dm-get-map (name-transform name)))))))
  "Returns a named map, loading it if necessary.  Unlike dm-get-map,
  this must be done inside a transaction.")

(defn bk-create-map!
  "Creates a new big-key table.  This cannot be done inside a
  transaction.  Returns nil."
  [name spec]
  (do
    (io!)
    (let [tname (name-transform name)
          dm-spec {:cols {:hashed_key (spec :key-type)
                          :val_hash (spec :val-type)}
                   :key :hashed_key}]
      (dm-dosync
       (dm-insert @*bk-maps* {:exname name
                             :spec spec}))
      (dm-create-map! tname dm-spec))))

(defn bk-delete
  "Deletes an entry from the map."
  [bkc keyval]
  (let [bk (bkc)
        dmap (get-dmap bk)
        abstract-keycol (get-abstract-keycol bk)
        munged-key ((get-key-munger bk) (pr-str keyval))]
    (if-let [dm-row (dmap munged-key)]
        (if ((dm-row :val_hash) keyval)
          (if (= 1 (count (dm-row :val_hash)))
            (dm-delete dmap munged-key)
            (dm-update dmap munged-key dissoc-in :val_hash keyval))
          (dm-ensure dmap munged-key))
      (dm-delete dmap munged-key))
    bkc))

; bk-ensure - like select, but also ensures the row does not change

(defn bk-ensure
  "Look up a row in a map, returning a hash, and ensuring its value
  does not change within the transaction.  If the row does not exist,
  this function throws an error.  Instead, use dm-delete to ensure
  that a row remains deleted throughout a transaction."
  [bkc keyval]
  ; Note: This ensures the whole dm row.
  (let [bk (bkc)
        munged-key ((get-key-munger bk) (pr-str keyval))]
    (or (if-let [dm-row (dm-ensure (get-dmap bk) munged-key)]
            (let [valcol (dm-row :val_hash)]
              (valcol keyval)))
        (throw (Exception. "bk-ensure: non-existent row")))))


(defn md5-munger
  "Returns a function that maps a string of arbitrary length to the first
  'width' characters of the hex-encoded MD5 sum of that string."
  [width]
  (fn [s]
    (let [md (java.security.MessageDigest/getInstance "MD5")
          dig (.digest md (.getBytes s))]
      (apply str (take width (hex-encode-bytes (seq dig)))))))


