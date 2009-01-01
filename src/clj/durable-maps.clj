
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

; misc

(defn assoc-new [m key value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if and only if the key doesn't already exist."
  (if (m key)
    m
    (assoc m key value)))

(defn- tm []
  (System/currentTimeMillis))

; dm-new-connection - set up a connection to whatever is storing the data


; dm-create-map - create a new table

(defn dm-create-map [name]
  "Create a new table.  This is not guaranteed to be thread-safe."
  ; XXX
  )


; dm-get-map

(defn- dm-load-table [name]
  "Load the given table into *tables*.  Returns nil."
  (swap! *tables* assoc-new name
         {:name name
          :write-hash (ref {})
          :write-queue (ref '())
          :read-hash (ref {})}))

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
  ""
  [dmap key]
  )

(defn- local-select
  "Look the key up in the given durable map, or return nil if it isn't
  there."
  [dmap key]
  ((dmap :read) key))

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

