
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
        util
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

(defvar *dm-transaction-times* (atom (sorted-set)))
(defvar *dm-transaction-level* 0)

(defvar- *tables* (atom {}))
(defvar- *dm-shutting-down* (atom nil))
(defvar- *dm-db* (java.sql.DriverManager/getConnection
                  "jdbc:mysql://localhost/dm?user=dm&password=nZe3a5dL"))
; TODO An engine like SQLite might be ideal

(declare dm-update
         dm-insert)


; internal low-level functions

(defn- assoc-new [m key value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to m if and only if the key doesn't already exist.  Return m."
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

(defn- assoc-new-or-die [m key value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if the key doesn't already exist; otherwise throw an
  Exception."
  (if (m key)
    (throw (Exception. (str "Key " key " already in map")))
    (assoc m key value)))

(defn- tm []
  "Return a time value such that each call to (tm) returns a number
  greater than or equal to all numbers previously returned.  The units
  of the time value are not specified."
  (System/currentTimeMillis))

(defn- to-row-hash
  [row-result]
  ; XXX
  )

(defn- get-row
  "Get a single row from a table, given a primary key column, and a
  value."
  [table keycol keyval]
  (to-row-hash (get-query *dm-db*
                          (str "SELECT * FROM " table " WHERE "
                               keycol " = ?")
                          [keyval]))))

(defn- column-format
  "Convert a spec map into a string suitable for putting between the
  parentheses of a CREATE TABLE statement."
  [spec]
  (let [cols  (spec :cols)
        name-type-seq  (map #(vec (name (first %))
                                  (first (second %)))
                            cols)
        name-type-clauses  (map (fn [[n t]] (str n " " t))
                                name-type-seq)
        keycol  (name (spec :key))
        key-clause  (if keycol
                      (str "PRIMARY KEY (" keycol ")"))
        clause-seq  (if keycol
                      (concat name-type-clauses (list key-clause))
                      name-type-clauses)]
    (apply str (interpose ", " clause-seq))))

(defn- create-table
  "Create a table, given a name and a :spec map.  An optional third
  parameter, if true, specifies that the table should be created with
  CREATE TABLE IF NEW."
  ([name spec]
     (create-table name spec false))

  ([name spec if-new]
     (let [cmd (if if-new "CREATE TABLE IF NOT EXISTS" "CREATE TABLE")
           cols (column-format spec)]
       (run-stmt *dm-db*
                 (apply str cmd " " name " (" cols ")")))))

(defn require-dmtrans
  "Throw an exception if not running inside a durable-maps
  transaction.  (Note: (io!) is sufficient to do the reverse.)"
  []
  (when-not (pos? *dm-transaction-level*)
    (throw (IllegalStateException.
            ("Call to transaction-only function outside (dm-dosync)")))))

; dm-create-map - create a new table

(defn- get-internal-table-map
  "Given a table's raw SQL name, and a map defining the columns of the
  table, return an internal durable map, without an external name, and
  not in *tables* or *table-map*."
  [rawname spec]
  {:name nil
   :table rawname
   :spec spec
   :write (ref {})
   :write-queue (ref (queue))
   :read (ref {})})

(defvar- *table-map*
  "Map containing the table index"
  (get-internal-table-map "mtables"
                          {:cols {:inname ["VARCHAR(32)"  :str]
                                  :exname ["VARCHAR(256)" :str]
                                  :spec   ["BLOB"         :obj]}
                           :key :exname}))

(defvar- *var-map*
  "Map containing arbitrary persistent variables"
  (get-internal-table-map "mvar"
                          {:cols {:varname ["VARCHAR(64)" :str]
                                  :val     ["BLOB"        :obj]}
                           :key :varname}))

(defn- next-inname-num
  "Return the next unique number to be used for internal table names."
  []
  (dm-dosync
   (dm-update (fn [] *var-map*) "inname-num" :val inc)))

(defn- collapse-name
  "Return a SQL-friendly object name based on the given name."
  [name]
  (apply str (take 16 (re-seq #"[a-zA-Z]" name))))

(defn dm-create-map!
  "Create a new table.  This cannot be done inside a transaction,
  because the table must be available for INSERTs upon return from
  this function, which requires IO."
  [name spec]
  (do
    (if (dm-select *table-map* name)
      (throw (Exception. (str "dm-create-map: Table exists: " name))))
    (let [safename (str "u"
                        (collapse-name name)
                        (next-inname-num))]
      (do
        (io!)
        (create-table safename spec)
        (dm-dosync
         (dm-insert (fn [] *table-map*)
                    {:inname safename
                     :exname name
                     :spec spec}))))))


; dm-get-map

(defn- dm-load-table
  "Load the existing table with the given name into *tables*.  Returns
  nil."
  [name]
  (let [table-map-entry (dm-select *table-map* name)]
    (swap! *tables* assoc-new name
           {:name name
            :table (table-map-entry :inname)
            :spec (table-map-entry :spec)
            :write (ref {})
            :write-queue (ref (queue))
            :read (ref {})})))

(defn dm-get-map
  "Return a named map, loading it if necessary.  The actual value
  returned is the function select closed over the map, so that it can
  be used as if it were an ordinary Clojure map.  This may be done in
  or out of a transaction."
  [name]
  (do
    (when-not (@*tables* name)
      (dm-load-table name))
    (let [t (@*tables* name)]
      (fn [& args] (apply dm-select t args)))))


; dm-insert - add a new map entry, failing if the entry already exists

(defn- form-write-query
  "Form an SQL query to insert the given row, a hash, into the table
  backing the given durable map."
  [dmap row]
  ; XXX
  )

(defn dm-insert
  "Add a new map entry, throwing an exception if an entry with the
  given key already exists."
  [dmap-c row] 
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)
          writes (dmap :write)
          write-query (form-write-query dmap row)]
      (commute writes assoc-new-or-die key row)
      (commute write-queue conj write-query))))

; dm-update - alter an existing map entry

(defn dm-update
  "Update the row with the given key, setting the value at col to (f
  oldval), and returning that new value."
  [dmap-c key col f]
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)]
      ; XXX
      )))

; dm-commute - commute an existing map entry

(defn dm-commute
  "Commute the row with the given key, setting the value at col to (f
  oldval), and returning that new value."
  [dmap-c key col f]
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)]
      ; XXX
      )))

; dm-select - look up something in a map

; get-map returns this function closed over a table handle so it can
; be used for lookup just like a regular Clojure map.

(defmulti translate-type (fn [val type] type))

(defmethod translate-type :str
  [val type]
  val)

(defmethod translate-type :obj
  [val type]
  ; FIXME - would prefer to use a non-evaluating read here
  (read-string (str val)))

(defn- translate-val
  [[col val] spec cols]
  (let [colspec (cols col)
        [_ coltype] colspec]
    (translate-type val coltype)))

(defn- mapify-row
  "Converts the key-value pairs pulled out of SQL for a row into the
  parsed and marked-up hash that represents the row in memory."
  [raw-row spec]
  (let [cols (spec :cols)]
    (assoc (map #(translate-val % spec cols) raw-row)
      :__spec spec)))

(defn- database-select
  "Get the row from the database for the given key, insert it in the
  read cache, and return it."
  [dmap key-value]
  (let [spec (dmap :spec)
        row (get-row (dmap :table)
                     (spec :key)
                     key-value)
        row-map (mapify-row row spec)
        reads (spec :read)]
    (commute reads assoc-new key-value row-map)))

(defn- local-select
  "Look the key up in the given durable map, or return nil if it isn't
  there."
  [dmap key]
  (or (@(dmap :read) key)
      (@(dmap :write) key)))

(defn- dm-select
  "Look something up in a map, returning a row hash, or nil if it is
  not found.  With one argument, returns that argument.  Unlike with
  Refs, there are no flying reads on durable maps.  This function must
  always be called inside a transaction."
  ([dmap key]
     (require-dmtrans)
     (or (local-select dmap key)
         (database-select dmap key)))
  ; Used internally to turn a closure back into the variable being
  ; closed over, since it is more convenient for dm-get-map to return
  ; a function with Clojure map lookup semantics:
  ([dmap]
     dmap))


; dm-dosync

(let [i (atom 0)]
  (defn- new-trans-id []
    (swap! i inc)))

(defn- swap-in-time [times]
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
  (do
    (if @*dm-shutting-down*
      (throw (IllegalStateException.
              "Attempted to begin transaction while shutting down.")))
    (binding [database-select (if (pos? *dm-transaction-level*)
                                database-select
                                (memoize database-select))
              *dm-transaction-level* (inc *dm-transaction-level*)]
      (let [trans-time (swap-in-time *dm-transaction-times*)]
        (try
         (dosync (f))
         (finally (swap! *dm-transaction-times* disj trans-time)))))))

(defmacro dm-dosync
  "Runs the exprs (in an implicit do) in a transaction that
  encompasses exprs and any nested calls.  The transaction provides a
  consistent and complete view of the durable-map universe, in
  addition to Clojure refs.  Modifications, once successful, are
  queued for writing to permanent storage."
  [& body]
  `(run-in-dm-transaction (fn [] ~@body)))

(defn dm-shutdown!
  "Flushes all pending writes and shuts down the database connection.
  Any attempts to write in any other thread after calling dm-shutdown
  will result in null-pointer exceptions."
  []
  (do
    (io!)
    (swap! @*dm-shutting-down* (fn [_] (tm)))
    ; XXX what else?
    ))


; Writer thread

(defn- flush-writes-older-than
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

