
;; <copyleft>

;; Copyright 2008 Dan Reish

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
;; library), containing parts covered by the terms of the Common
;; Public License, the licensors of this Program grant you additional
;; permission to convey the resulting work. {Corresponding Source for
;; a non-source form of such a combination shall include the source
;; code for the parts of clojure-contrib used as well as that of the
;; covered work.}

;; </copyleft>

(ns bigkey-dm
  (:use clojure.contrib.def
        durable-maps))

; Takes a name and a spec defining how a pseudo-key column (which may
; be text or blob) is to be transformed to something fixed-length, and
; returns a durable-map-style interface on that (which participates in
; durable-map transactions).

(defvar- get-dmap :dmap)
(defvar- get-key-munger :key-munger)
(defvar- get-abstract-keycol :abstract-keycol)

(defn bk-select
  [bk k]
  (let [munged-key ((get-key-munger bk) k)]
    (if-let [dm-row ((get-dmap bk) munged-key)]
        (let [valcol (dm-row :val_hash)]
          (valcol k))))

(defn- close-bk
  [bk]
  (fn
    ([] bk)
    ([k] (bk-select bk k))))

(defn- name-transform
  [name]
  (str "bigkey-dm/" name))

(defn bk-insert
  [bkc row]
  (let [bk (bkc)
        dmap (get-dmap bk)
        abstract-keycol (get-abstract-keycol bk)
        k (row abstract-keycol)
        munged-key ((get-key-munger bk) k)]
    (if-let [dm-row ((dmap munged-key) :val_hash)]
        (if (dm-row (row abstract-keycol))
          (throw (Exception. "Insert with already-existing key"))
          (dm-update dmap munged-key assoc k row))
      (dm-insert dmap {:hashed_key munged-key,
                       :val_hash {k row}}))))

(defn bk-update
  [bkc keyval f & args]
  (let [bk (bkc)
        dmap (get-dmap bk)
        abstract-keycol (get-abstract-keycol bk)
        k (row abstract-keycol)
        munged-key ((get-key-munger bk) k)]
    (if-let [dm-row ((dmap munged-key) :val_hash)]
        (if-let [bk-row (dm-row k)]
            (let [new-bk-row (apply f bk-row args)]
              (dm-update dmap munged-key assoc k new-bk-row))
          (throw (Exception. "Update on non-existent row")))
      (throw (Exception. "Update on non-existent row")))))
  
(defn bk-create-map!
  "Create a new big-key table.  This cannot be done inside a
  transaction."
  [name spec]
  (let [tname (name-transform name)]
    
    ; XXX
    ))