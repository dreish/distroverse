
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

(ns durable-maps
  (:use clojure.contrib.def
;       clojure.contrib.sql :as sql
        sql))

; Global, named maps that survive from one run to the next and are
; DECIDEDLY MUTABLE, but only within transactions, and are NOT
; ITERABLE.

; One important caveat with this library is that it does not provide
; the same guarantee that SQL does, that at the end of a transaction,
; the committed data will exist permanently on disk.  In the case of a
; write to a durable map, the write to disk may happen an arbitrary
; amount of time later than that.  (TODO provide an await function)
; Programs using this library must call dm-shutdown before exiting, or
; they will hang.

; I'm (ab)using SQL as an implementation detail.  I'm not really using
; the vast majority of features of SQL, so this could just as well be
; done with flat files.  Even worse, I'm hogging the tables for
; myself; much of this code WILL BREAK if any other process alters the
; tables (since I'm caching values in memory and all my transactions
; and consistency checks are strictly on those in-memory values).
; Doing it with a database makes it a little easier to scale to
; ridiculous numbers by throwing money at the problem, though.

(defvar- *tables* (atom {}))
(defvar- *dm-shutting-down* (atom nil))
(defvar *dm-transaction-times* (atom (sorted-set)))
(defvar *dm-transaction-level* 0)
(defvar- *dm-db* (java.sql.DriverManager/getConnection
                  "jdbc:mysql://localhost/dm?user=dm&password=nZe3a5dL"))


; internal low-level functions

(defn- assoc-new [m key value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if and only if the key doesn't already exist."
  (if (m key)
    m
    (assoc m key value)))

(defn- assoc-new-or-retry [m key value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if the key doesn't already exist; otherwise throw a
  RetryEx."
  (if (m key)
    (clojure.lang.RetryTransaction/retry)
    (assoc m key value)))

(defn- tm []
  "Return a time value such that each call to (tm) returns a number
  greater than or equal to all numbers previously returned.  The units
  of the time value are not specified."
  (System/currentTimeMillis))


(defn- get-row
  "Get a single row from a table, given a primary key column, and a
  value."
  [table keycol keyval]
  (first (:rows (get-query *dm-db*
                           (str "SELECT * FROM " table " WHERE "
                                keycol " = ?")
                           keyval))))

(defn- column-format
  "Convert a spec map into a string suitable for putting between the
  parentheses of a CREATE TABLE statement."
  [spec]
  (let [cols  (spec :cols)
        name-type-seq  (map #(list (name (first %))
                                   (first (second %)))
                            cols)
        name-type-clauses  ; XXX
        key  (name (spec :key))
        key-clause  (if key
                      (str "PRIMARY KEY (" key ")"))
        clause-seq  (if key
                      (concat name-type-clauses (list key-clause))
                      name-type-clauses)]
    (apply str (interpose ", " clause-seq))))

(defn- create-table
  "Create a table, given a name and a :spec map.  An optional third
  parameter, if true, specifies that the table should be create with
  CREATE TABLE IF NEW."
  ([name spec]
     (create-table name spec false))

  ([name spec if-new]
     (let [cmd (if if-new "CREATE TABLE IF NOT EXISTS" "CREATE TABLE")
           cols (column-format spec)]
       (run-stmt *dm-db*
                 (apply str cmd " " name " (" cols ")")))))

; dm-create-map - create a new table

(defn- get-table-map
  "Given a table's raw SQL name, and a map defining the columns of the
  table, return a durable map."
  [name spec]
  ())

(defvar- *table-map*
  ""
  (get-table-map "mtables"
                 {:cols {:inname ["VARCHAR(32)"  :str]
                         :exname ["VARCHAR(256)" :str]
                         :spec   ["BLOB"         :map]}
                  :key :exname}))

(defn dm-create-map
  "Create a new table.  This is not guaranteed to be thread-safe."
  [name spec]
  ; XXX
  )


; dm-get-map

(defn- dm-load-table [name]
  "Load the given table into *tables*.  (This is stateless from SQL's
  point of view.)  Returns nil."
  (swap! *tables* assoc-new name
         {:name name
          :table 
          :write (ref {})
          :write-queue (ref [])
          :read (ref {})}))

(defn dm-get-map [name]
  "Return a named map, loading it if necessary.  The actual value
  returned is the function select closed over the map, so that it can
  be used as if it were an ordinary Clojure map."
  (do
    (when-not (@*tables* name)
      (dm-load-table name))
    (let [t (@*tables* name)]
      (fn [& args] (apply select t args)))))


; dm-insert - add a new map entry 

; CAUTION: This is the least concurrent action, because global tables
; are altered instead of commuted.  Use it only on maps where the keys
; are meaningful such as usernames or public keys, rather than
; guaranteed-unique serial numbers.


; dm-commute - add a new map entry assuming key collisions are impossible


; dm-update - modify an existing map entry


; dm-select - look up something in a map

; get-map returns this function closed over a table handle so it can
; be used for lookup just like a regular Clojure map.

(defn- database-select
  "Get the key from the database."
  [dmap key]
  ; XXX
  )

(defn- local-select
  "Look the key up in the given durable map, or return nil if it isn't
  there."
  [dmap key]
  (or ((dmap :read) key)
      ((dmap :write) key)))

(defn dm-select
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


; dm-sync

(let [i (atom 0)]
  (defn- new-trans-id []
    (swap! i inc)))

(defn swap-in-time [times]
  "Create a vector with the current time and a unique transaction
  identifier, swap-conj that into *dm-transaction-times*, and return
  the vector."
  (let [trans-time [(tm) (new-trans-id)]]
    (swap! times conj trans-time)
    trans-time))

(defn run-in-dm-transaction
  "Runs the given no-argument function in a transaction that
  encompasses exprs and any nested calls.  The transaction provides a
  consistent and complete view of the durable-map universe, in
  addition to Clojure refs.  Modifications, once successful, are
  queued for writing to permanent storage."
  [f]
  (binding [database-select (if (pos? *dm-transaction-level*)
                              database-select
                              (memoize database-select))
            *dm-transaction-level* (inc *dm-transaction-level*)]
    (let [trans-time# (swap-in-time *dm-transaction-times*)]
      (try
       (dosync (f))
       (finally (swap! *dm-transaction-times* disj trans-time#))))))

(defmacro dm-sync
  "Runs the exprs (in an implicit do) in a transaction that
  encompasses exprs and any nested calls.  The transaction provides a
  consistent and complete view of the durable-map universe, in
  addition to Clojure refs.  Modifications, once successful, are
  queued for writing to permanent storage."
  [& body]
  `(run-in-dm-transaction (fn [] ~@body)))

(defn dm-shutdown
  "Flushes all pending writes and shuts down the database connection.
  Any attempts to write in any other thread after calling dm-shutdown
  will result in null-pointer exceptions."
  []
  (do
    (swap! @*dm-shutting-down* (fn [] true))
    ; XXX what else?
  )


; Writer thread

(defn flush-writes-older-than
  "Flush writes older than the given time value.  Returns the number
  of writes committed to permanent storage."
  [t]
  ; XXX
  )

(defvar- writer-thread
  (Thread.
     #(loop
        (let [oldest-trans-time (or (ffirst @*dm-transaction-times*)
                                    (tm))
              number-written (flush-writes-older-than oldest-trans-time)]
          (dosync
           ; XXX  remove writes from queue (what var?) here
           )
          (Thread/sleep 5000)
          (when-not @*dm-shutting-down*
            (recur))))
     "writer-thread")
  "Thread that wakes every five seconds and polls for writes old
  enough that they are no longer entangled in any ongoing
  transactions, writing them to disk and removing them from the write
  queue.")

(.start writer-thread)

