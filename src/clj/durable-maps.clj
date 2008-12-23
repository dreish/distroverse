
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

(ns 'durable-maps)
(use 'clojure.contrib.def)

; Global, named maps that survive from one run to the next and are
; DECIDEDLY MUTABLE, but only within transactions, and are NOT
; ITERABLE.

; I'm (ab)using SQL as an implementation detail.  I'm not really using
; the vast majority of features of SQL, so this could just as well be
; done with flat files.  Even worse, I'm hogging the tables for
; myself; much of this code WILL BREAK if any other process alters the
; tables (since I'm caching values in memory and all my transactions
; and consistency checks are strictly on those in-memory values).
; Doing it with a database makes it a little easier to scale to
; ridiculous numbers by throwing money at the problem, though.

(defvar- *tables* (atom {}))


; new-connection - set up a connection to whatever is storing the data


; create-map - create a new table

(defn create-map [name]
  "Create a new table.  This is not guaranteed to be thread-safe."
  ; XXX
  )


; get-map

(defn assoc-new [m key value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if and only if the key doesn't already exist."
  (if (m key)
    m
    (assoc m key value)))

(defn- load-table [name]
  "Load the given table into *tables*.  Returns nil."
  (swap! *tables* assoc-new name
         {:name name
          :write-hash (ref {})
          :write-queue (ref '())
          :read-hash (ref {})}))

(defn get-map [name]
  "Return a named map, loading it if necessary.  The actual value
  returned is the function select closed over the map, so that it can
  be used as if it were an ordinary Clojure map."
  (when-not (@*tables* name)
    (load-table name))
  (let [t (@*tables* name)]
    (fn [& args] (apply select t args))))


; insert - add a new map entry 

; CAUTION: This is the least concurrent action, because global tables
; are altered instead of commuted.  Use it only on maps where the keys
; are meaningful such as usernames or public keys, rather than
; guaranteed-unique serial numbers.


; commute - add a new map entry assuming key collisions are impossible


; update - modify an existing map entry


; select - look up something in a map

; get-map returns this function closed over a table handle so it can
; be used for lookup just like a regular Clojure map.

(defn select
  "Look something up in a map.  With one argument, returns that
  argument."
  ([dmap key]
     (or (local-select dmap key)
         (database-select dmap key)))
  ; Used internally to turn a closure back into the variable being
  ; closed over, since it is more convenient for get-map to return a
  ; function with Clojure map lookup semantics:
  ([dmap]
     dmap))


; dmsync

(defmacro dmsync
  "Runs the exprs (in an implicit do) in a transaction that
  encompasses exprs and any nested calls.  The transaction provides a
  consistent view of the durable-map universe, in addition to Clojure
  refs.  Modifications, once successful, are queued for writing to
  permanent storage."
  [& body]
  ; XXX
  )


(defn dm-shutdown []
  "Flushes all pending writes and shuts down the database connection.
  Any attempts to write in any other thread after calling dm-shutdown
  will result in null-pointer exceptions."
  ; XXX 
  )