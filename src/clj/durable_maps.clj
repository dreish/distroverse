
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

(ns durable-maps
  (:require [datastore :as ds])
  (:use clojure.contrib.def
        clojure.contrib.duck-streams
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
; Programs using this library must call shutdown! before exiting,
; or they will hang.

; I'm (ab)using SQL as an implementation detail.  I'm not really using
; the vast majority of features of SQL, so this could just as well be
; done with flat files.  Even worse, I'm hogging the tables for
; myself; much of this code WILL BREAK if any other process alters the
; tables (since I'm caching values in memory and all my transactions
; and consistency checks are strictly on those in-memory values).
; Doing it with a database makes it a little easier to scale to
; ridiculous numbers by throwing money at the problem, though.

(defvar- *transaction-times* (atom (sorted-set)))
(defvar- *transaction-level* 0)

(defvar- *tables* (atom {}))
(defvar- *write-queue* (ref (queue)))
(defvar- *writer* (agent true))
(defvar- *shutting-down* (ref nil))
(defvar- *aborting* (atom nil))
(defvar- *ds-open-args* (ref nil))
(defvar- *db* (ref nil))
(defvar- *schema-change-mutex* (Object.))
; XXX convenient, but probably doesn't belong here
(Class/forName "com.mysql.jdbc.Driver")


(declare update
         database-select
         local-select
         select
         flush-writes-before!
         agent-write!)

(defn assoc-new [m mkey value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to m if and only if the key doesn't already exist.  Return m."
  (if (m mkey)
    m
    (assoc m mkey value)))

(defn assoc-new-or-retry [m mkey value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if the key doesn't already exist; otherwise throw a
  RetryEx."
  (if (m mkey)
    (clojure.lang.RetryTransaction/retry)
    (assoc m mkey value)))

(defn assoc-new-or-die [m mkey value]
  "Given a Clojure map, a key, and a value, add the key-value mapping
  to the m if the key doesn't already exist; otherwise throw an
  Exception."
  (if (m mkey)
    (throw (Exception. (str "Key " mkey " already in map")))
    (assoc m mkey value)))

; internal low-level functions

(defn require-dmtrans
  "Throw an exception if not running inside a durable-maps
  transaction.  (Note: (io!) is sufficient to do the reverse.)"
  []
  (when-not (pos? *transaction-level*)
    (throw (IllegalStateException.
            "Call to transaction-only function outside (dmsync)"))))

; create-map! - create a new table

(defn- get-internal-table-map
  "Given a table's raw SQL name, and a map defining the columns of the
  table, return an internal durable map, without an external name, and
  not in *tables* or *table-map*."
  [rawname spec]
  {:name nil
   :table rawname
   :spec spec
   :write (ref {})
   :read (ref {})})

(defvar- *table-map*
  (get-internal-table-map "mtables"
                          {:cols {:inname ["VARCHAR(32)"  :str]
                                  :exname ["VARCHAR(256)" :str]
                                  :spec   ["TEXT"         :obj]}
                           :key :exname})
  "Map containing the table index")

(defvar- *var-map*
  (get-internal-table-map "mvar"
                          {:cols {:varname ["VARCHAR(64)" :str]
                                  :val     ["TEXT"        :obj]}
                           :key :varname})
  "Map containing arbitrary persistent variables")

; dmsync

(let [i (atom 0)]
  (defn- new-trans-id []
    "Generate a unique transaction ID to guarantee an unambiguous
  order of transactions."
    (swap! i inc)))

(defn- swap-in-time [times]
  "Create a vector with the current time and a unique transaction
  identifier, swap-conj that into *transaction-times*, and return
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
  ; TODO could probably replace *transaction-level* with a boolean,
  ; and when true, just call f.  This could even make flying reads
  ; reasonable.
  (do
    (binding [database-select (if (pos? *transaction-level*)
                                database-select
                                (memoize database-select))
              *transaction-level* (inc *transaction-level*)]
      (let [trans-time (swap-in-time *transaction-times*)
            ret (try
                 (dosync
                  (if (ensure *shutting-down*)
                    (throw (IllegalStateException.
                            #=(str "Transaction attempted"
                                   " while shutting down."))))
                  (f))
                 (finally (swap! *transaction-times*
                                 disj trans-time)))]
        (when (= 1 *transaction-level*)
          (send *writer* agent-write!))
        ret))))

(defmacro dmsync
  "Runs the exprs (in an implicit do) in a transaction that
  encompasses exprs and any nested calls.  The transaction provides a
  consistent and complete view of the durable-map universe, in
  addition to Clojure refs.  Modifications, once successful, are
  queued for writing to permanent storage."
  [& body]
  `(run-in-dm-transaction (fn [] ~@body)))

; more misc

(defn- close-dmap
  [dmap]
  (fn
    ([] dmap)
    ([k] (select dmap k))))

(defn- next-inname-num
  "Return the next unique number to be used for internal table names."
  []
  (dmsync
   (-> (close-dmap *var-map*)
       (update "inname-num" update-in [:val] inc)
       :val)))

; get-map

(defn- load-table
  "Load the existing table with the given name into *tables*.  Returns
  @*tables*.  If the table happens to be loaded, this function has no
  effect."
  [name]
  (let [def-spec (ds/default-spec @*db*)
        table-map-entry (when-not def-spec
                          (dmsync (select *table-map* name)))]
    (if (or def-spec table-map-entry)
      (let [table-name (if def-spec
                         name
                         (table-map-entry :inname))
            table-spec (if def-spec
                         def-spec
                         (table-map-entry :spec))]
        (swap! *tables* assoc-new name
               {:name name
                :table table-name
                :spec table-spec
                :write (ref {})
                :read (ref {})})))))

(defn get-map
  "Return a named map, loading it if necessary.  The actual value
  returned is the function select closed over the map, so that it can
  be used as if it were an ordinary Clojure map.  This may be done in
  or out of a transaction."
  [name]
  (do
    (when-not (@*tables* name)
      (load-table name))
    (if-let [t (@*tables* name)]
        (close-dmap t))))

; insert - add a new map entry, failing if the entry already exists

(defn- add-to-write-queue
  ([write-query]
     ;; TODO do some basic validation here; at least make sure there
     ;; are no nils in :vals
     (let [dmap (write-query :dmap)
           row (write-query :row)
           keycol (-> dmap :spec :key)]
       (when (and row (not (exists? row keycol)))
         (throw (Exception. (str "Attempted to add a row to the write"
                                 " queue with no key column")))))
     (commute *write-queue* conj write-query)))

(defn- fixrow
  "Regenerate any derived parts of the in-memory representation of a
  row, returning the corrected row."
  [newrow oldrow cols-to-check]
  (if (nil? cols-to-check)
    newrow
    (let [col (first cols-to-check)
          more-cols (next cols-to-check)
          [colname checker fixer] col
          checker (resolve checker)     ; TODO do this only once
          fixer (resolve fixer)]        ; TODO do this only once
      (if (and oldrow
               (checker (newrow colname)
                        (oldrow colname)))
        (recur newrow oldrow more-cols)
        (let [new-newrow (assoc newrow
                           colname (fixer (newrow colname)))]
          (recur new-newrow oldrow more-cols))))))

(defn seq-check
  [newcol oldcol]
  (= (newcol :func) (oldcol :func)))

(defn seq-fix
  [colval]
  (let [func (colval :func)]
    (assoc colval
      :seq (lazy-seq (apply (resolve (first func))
                            (rest func))))))

(defn insert
  "Add a new map entry, throwing an exception if an entry with the
  given key already exists.  Returns dmap-c."
  [dmap-c row] 
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)
          row (fixrow row nil (-> dmap :spec :checked-cols))
          writes (dmap :write)
          rowkey (row (-> dmap :spec :key))
          in-writes (writes rowkey)
          write-query {:type :insert
                       :dmap dmap
                       :time (tm)
                       :required true
                       :row row}]
      (if (dmap-c rowkey)
        (throw (Exception. (str "insert: Row exists: " rowkey))))
      (if in-writes
        (ref-set in-writes row)
        (commute writes assoc-new-or-retry rowkey (ref row)))
      (add-to-write-queue write-query))
    dmap-c))

; update - alter an existing map entry

(defn update
  "Update the row with the given key with the same concurrency
  behavior as alter, setting the value at col to (f oldrow), and
  returning that new value.  The row must already exist.  The key
  column may not be changed."
  [dmap-c keyval f & args]
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)
          writes (dmap :write)
          in-writes (@writes keyval)
          oldrow (dmap-c keyval)
          keycol (-> dmap :spec :key)]
      (when-not oldrow
        (throw (Exception.
                (str "update: No existing row for key " keyval))))
      (let [newrow (apply f oldrow args)
            newrow (fixrow newrow oldrow (-> dmap :spec :checked-cols))
            ; TODO delay (update-query); will not always be needed
            ; write-query (update-query dmap newrow)
            write-query {:type :update
                         :dmap dmap
                         :time (tm)
                         :required true
                         :newrow newrow}]
        (if (not= (oldrow keycol) (newrow keycol))
          (throw (Exception.
                  "update attempted to change key")))
        (if in-writes
          (ref-set in-writes newrow)
          (commute writes assoc-new-or-retry keyval (ref newrow)))
        (add-to-write-queue write-query)
        newrow))))

; select - look up something in a map

; get-map returns this function closed over a table handle so it can
; be used for lookup just like a regular Clojure map.

(defn- database-select
  "Get the row from the database for the given key, insert it in the
  read cache, and return it."
  [dmap key-value]
  (let [spec (dmap :spec)
        row-map (ds/getrec @*db*        ; not row
                           dmap
                           (name (spec :key))
                           key-value)
        row-map-ref (ref row-map)
        reads (dmap :read)]
    (when (pos? (count row-map))
      (commute reads assoc-new key-value row-map-ref)
      row-map-ref)))

(defn- local-select
  "Look the key up in the given durable map, or return nil if it isn't
  there."
  [dmap keyval]
  (or (@(dmap :write) keyval)
      (@(dmap :read) keyval)))

(defn- select
  "Look something up in a map, returning a row hash, or nil if it is
  not found.  Unlike with Refs, there are no flying reads on durable
  maps.  This function must always be called inside a transaction."
  ([dmap keyval]
     (require-dmtrans)
     (if-let [selected (or (local-select dmap keyval)
                           (database-select dmap keyval))]
         @selected
       nil)))

; selock - like select, but also ensures the row does not change

(defn selock
  "Look up a row in a map, returning a hash, and ensuring its value
  does not change within the transaction.  If the row does not exist,
  this function throws an error.  Instead, use delete to ensure that a
  row remains deleted throughout a transaction."
  [dmap-c keyval]
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)
          selected (or (local-select dmap keyval)
                       (database-select dmap keyval))]
      (if (and selected @selected)
        (ensure selected)
        (throw (Exception. "selock: non-existent row"))))))


; delete - remove a key from a map

(defn delete
  "Deletes the row with the given key with the same concurrency
  behavior as altering the row.  If the row does not currently exist,
  this function either prevents a concurrent transaction from creating
  such a row, or fails (retrying the transaction)."
  [dmap-c keyval]
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)
          writes (dmap :write)
          in-writes (@writes keyval)
          ;write-query (delete-query dmap keyval)
          ]
      (if in-writes
        (ref-set in-writes nil)
        (commute writes assoc-new-or-retry keyval (ref nil)))
      ;(add-to-write-queue write-query)
      (add-to-write-queue {:type :delete
                           :dmap dmap
                           :time (tm)
                           :required true
                           :keyval keyval})
      dmap-c)))

; pkeys - return a seq of primary keys for the given map

(defn pkeys
  "Returns a seq of primary keys for all rows in the given map.
  In much the same way as a function that uses line-seq might, pkeys
  allocates a resource that will not be freed until the entire seq is
  consumed.  BE SURE TO CONSUME THE ENTIRE SEQUENCE WITHIN A SINGLE
  TRANSACTION."
  [dmap-c]
  (do
    (require-dmtrans)
    (let [dmap (dmap-c)
          keyname (-> dmap :spec :key name)]
      ;; Should probably try to do something to ensure that the whole
      ;; sequence is used within a single transaction, but how?
      (concat (filter #(-> dmap :write deref (get %) deref nil? not)
                      (keys @(dmap :write)))
              (filter #(and (-> dmap :write deref (get %) not)
                            (-> dmap :read deref (get %) deref nil? not))
                      (keys @(dmap :read)))
              (filter #(and (-> dmap :write deref (get %) not)
                            (-> dmap :read deref (get %) not))
                      (ds/get-pkey-seq @*db* dmap keyname))))))

(defn create-map!
  "Create a new table.  This cannot be done inside a transaction.
  Does not check that column names, given as keywords, are legal SQL
  column names.  Returns nil.  Throws an exception if the given map
  already exists."
  ([name spec]
     (create-map! name spec false))
  ([name spec only-if-new]
     (when-not (ds/valid-spec @*db* spec)
       (throw (Exception. "create-map: Invalid spec")))
     (when-not (ds/default-spec @*db*)
       (io!)
       (locking *schema-change-mutex*
         (if (dmsync (select *table-map* name))
           (when-not only-if-new
             (throw (Exception. (str "create-map: Table exists: "
                                     name))))
           (when-not (ds/default-spec @*db*)
             (let [safename (str (ds/munge @*db* name)
                                 (next-inname-num))]
               (do
                 (ds/newtable! @*db* safename spec)
                 (dmsync
                  (insert (close-dmap *table-map*)
                          {:inname safename
                           :exname name
                           :spec spec}))
                 nil))))))))

(defn create-new-map!
  "Create a new table.  This cannot be done inside a transaction.
  Does not check that column names, given as keywords, are legal SQL
  column names.  Returns nil.  Does nothing if the given map already
  exists."
  ([name spec]
     (create-map! name spec true)))

(defn drop-map!
  "Delete a map.  Requires that the key column name (a keyword) be
  given as a safety measure."
  ([name k]
     (io!)
     (locking *schema-change-mutex*
       (if-let [t (dmsync (select *table-map* name))]
         (do
           (when-not (-> t :spec :key (= k))
             (throw (Exception. (str "Key " k " is not correct for table "
                                     name))))
           (ds/drop! @*db* (t :inname))
           (if-let [other-t (@*tables* name)]
             (dosync
              (ref-set (other-t :write) "fail")
              (ref-set (other-t :read)  "fail")))
           (swap! *tables* dissoc name)
           (dmsync (delete (close-dmap *table-map*)
                           name))
           nil)))))

; init! - create core tables 'mtables' and 'mvar'

(defn init!
  "Creates the core internal tables used by durable-maps to store
  persistent metadata.  It is safe to call this function on an
  existing database; it will have no effect."
  []
  (do
    (ds/newtable! @*db* "mtables" (*table-map* :spec) true)
    (ds/newtable! @*db* "mvar"    (*var-map*   :spec) true)
    (dmsync
     (if (not (select *var-map* "inname-num"))
       (insert (close-dmap *var-map*)
               {:varname "inname-num"
                :val 1})))
    nil))


; Writer thread

(defn- get-next-write-before
  "Return the next write before time t that either must be done, or
  still reflects the up-to-date data in the object being written.
  Remove from the queue all writes up to that one."
  [t]
  (dosync
   (loop []
     (let [q (first @*write-queue*)]
       (if (or (not q)
               (>= (q :time) t))
         nil
         (do
           (alter *write-queue* pop)
           ; Send/send-off callbacks here
           (if (or (q :required)
                   (= (q :row)
                      @((-> q :dmap :write deref) (q :keyval))))
             q
             (recur))))))))

(defn- data-delete!
  [w]
  (ds/delete! @*db*
              (w :dmap)
              (-> w :dmap :spec :key)
              (w :keyval)))

(defn- data-insert!
  [w]
  (ds/add! @*db*
           (w :dmap)
           (w :row)))

(defn- data-update!
  [w]
  (ds/change! @*db*
              (w :dmap)
              (w :newrow)))

;; Would prefer to use a defmulti-, but there's no such thing
(let [impl-map {:delete data-delete!
                :insert data-insert!
                :update data-update!}]
  (defn- data-write!
    "Execute an internal write object."
    [w]
    (if-let [impl (impl-map (w :type))]
      (impl w))))

(defn- flush-writes-before!
  "Flush writes older than the given time value.  Returns the number
  of writes committed to permanent storage."
  [t]
  (io!
   (loop []
     (if-let [next-write (get-next-write-before t)]
       (do
         (try
          (data-write! next-write)
          (catch Exception e
            (throw (Exception.
                    (str "Database write failed: "
                         (pr-dup
                          (pr-str (assoc next-write
                                    :dmap-name (-> next-write :dmap :name)
                                    :dmap nil))))
                    e))))
         (recur))))))


(defn flush-ready-writes!
  "Flush all database-writing commands that are eligible to be
  executed."
  []
  (if (pos? (count @*write-queue*))
    (let [current-time (tm)
          ; Must call (tm) before checking
          ; *transaction-times* to ensure that
          ; oldest-trans-time will be before any transaction
          ; started after *transaction-times* is checked.
          oldest-trans-time (or (ffirst @*transaction-times*)
                                current-time)]
      (flush-writes-before! oldest-trans-time))))

;; TODO - for increased joy, get rid of the polling thread ...
;; (defvar- writer-thread
;;   (Thread.
;;      #(loop []
;;         (flush-ready-writes!)
;;         (Thread/sleep 2000)
;;         (when-not @*shutting-down*
;;           (recur)))
;;      "writer-thread")
;;   "Thread that wakes every two seconds and polls for writes old enough
;;   that they are no longer entangled in any ongoing transactions,
;;   writing them to disk and removing them from the write queue.  Thread
;;   exits when *shutting-down* is true.")

(defn abort!
  "Shuts down the database connection immediately, dumping all pending
  writes to a log file."
  ([]
     (abort! "<no message given>"))
  ([msg]
     (io!)
     (dosync (ref-set *shutting-down* (tm)))
     (when (compare-and-set! *aborting* nil true)
       (pr-dup
        (write-lines (writer (str "aborted-writes-log-" (tm) "-" (pid)))
                     (list* msg
                            (strcat (take 71 (cycle [\- \^])))
                            (map pr-str @*write-queue*)))))))

;; ... and replace it with this:
(defn- agent-write!
  ([writer]
     (and writer
          (not @*shutting-down*)
          (not @*aborting*)
          (try (flush-ready-writes!)
               true
               (catch Exception e
                 (abort! (stack-trace-as-str e))
                 false)))))

(defn shutdown!
  "Flushes all pending writes and shuts down the database connection.
  Any attempts to write in any other thread after calling shutdown
  will result in null-pointer exceptions."
  []
  (do
    (io!)
    (dosync (ref-set *shutting-down* (tm)))
    (flush-writes-before! (tm))
    (assert (not (seq @*write-queue*)))))

(defn startup!
  "Starts the database, if it hasn't already been started.  Can be
  used to restart after an abort or shutdown."
  ([& ds-args]
     (let [new-conn (apply ds/open! ds-args)]
       (try
        (dosync
         (if (and (ensure *ds-open-args*)
                  (not= @*ds-open-args* ds-args))
           (throw (Exception. (str "startup! called with different"
                                   " arguments than previously")))
           (if (= nil (ensure *db*))
             (do (ref-set *db* new-conn)
                 ;;(when-not (.isAlive writer-thread)
                 ;;  (.start writer-thread))
                 (ref-set *ds-open-args* ds-args)
                 (ref-set *shutting-down* nil)
                 (reset! *aborting* nil)
                 nil)
             (ds/close! new-conn))))
        (catch Exception e
          (do 
            (ds/close! new-conn)
            (throw e)))))))

