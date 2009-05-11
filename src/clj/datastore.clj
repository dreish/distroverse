
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

(ns datastore
  (:use clojure.contrib.def
        util
        sql
;        dcookies
        dvtp-lib))

(import '(org.distroverse.dvtp GetCookie Cookie Str))

; Simple interface for an arbitrary, possibly asynchronous data
; storage backend, preferably supporting simultaneous atomic
; modifications of multiple records.


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-open!

(defmulti ds-open!
  "Return a datastore connection object (the first argument to all the
  other datastore functions).  If the first argument is :sql, the
  remaining arguments give the database name, username, and password.
  localhost and mysql are (currently) assumed.  If the first argument
  is :dcookies, the second argument is a DvtpChannel object that will
  take GetCookie messages, and the third argument is a callback adder
  function such as add-callback!."
  firstarg)

(defmethod ds-open! :sql
  [ds-type db user password]
  {:db (get-sql-conn
        (str "jdbc:mysql://localhost/" db "?user=" user
             "&password=" password))
   :datastore ds-type})


(defn on-datastore
  "Method dispatch function, based on the :datastore value of the
  first argument"
  ([ds & more]
     (ds :datastore)))


(defmulti translate-type-in
  "Translate an object read in from a database into an object for the
  hash."
  (fn [s type] type))

(defmulti translate-type-out
  "Translate an object in a hash into an object to be stored in the
  database."
  (fn [o type] type))

(defmethod translate-type-in :str
  [s type]
  s)

(defmethod translate-type-out :str
  [o type]
  o)

(defmethod translate-type-in :obj
  [s type]
  ; FIXME - would prefer to use a non-evaluating read here
  (read-string (str s)))

(defmethod translate-type-out :obj
  [o type]
  (pr-str o))

(defmethod translate-type-in :keyword
  [s type]
  (keyword s))

(defmethod translate-type-out :keyword
  [o type]
  (name o))

(defmethod translate-type-in :seq
  [s type]
  (let [func (read-string (str s))]
    {:func func
     :seq (lazy-seq (apply (resolve (first func))
                           (rest func)))}))

(defmethod translate-type-out :seq
  [o type]
  (pr-str (o :func)))

(defn- translate-val-in
  [[col val] cols]
  (let [colspec (cols col)
        [_ coltype] colspec]
    [col (translate-type-in val coltype)]))

(defn- translate-val-out
  [col val cols]
  (let [colspec (cols col)
        [_ coltype] colspec]
    (translate-type-out val coltype)))

(defn- mapify-row
  "Converts the key-value pairs pulled out of SQL for a row into the
  parsed and marked-up hash that represents the row in memory."
  [raw-row spec]
  (if raw-row
    (let [cols (spec :cols)]
      (apply assoc {}
             (apply concat (map #(translate-val-in % cols)
                                raw-row))))))

(defn- valify-row
  "Converts the given map into a sequence of vals that can be passed
  to sql/run-stmt!"
  [row-map spec cols]
  (let [colspecs (spec :cols)]
    (map #(translate-val-out % (row-map %) colspecs)
         cols)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-get

(defmulti ds-get
  "Get the record from the given table with the given key, returning a
  hash.  If no keycol is given, use the table's primary key."
  on-datastore)

(defn- to-row-hash
  "Convert a result hash from sql/get-query to a hash for a single row."
  [row-result]
  (if (pos? (count (row-result :rows)))
    (zipmap (map keyword (row-result :colnames))
            (first (row-result :rows)))))

(defn- get-row
  "Get a single row from a table, given a primary key column, and a
  value."
  [db table spec keycol keyval]
  (mapify-row (to-row-hash (get-query db
                                      (str "SELECT * FROM " table
                                           " WHERE " keycol " = ?")
                                      [keyval]))
              spec))

(defn get-pk
  "Get the primary key for the given table."
  [_ _]
  (throw (Exception. "Unimplemented function: get-pk")))

(defmethod ds-get :sql
  ([ds table keycol keyval]
     (get-row (ds :db)
              (table :table)
              (table :spec)
              keycol
              keyval))
  ([ds table keyval]
     (ds-get ds table (get-pk ds table) keyval)))


(defmethod ds-get :dcookies
  ([ds table keycol keyval]
     (if (not= :k keycol)
       (throw (Exception. (str "datastore :dcookies does not support key"
                               " columns other than :k")))
       (ds-get ds table keyval)))
  ([ds table keyval]
     ;; XXX this is going to require a synchronous wrapper around the
     ;; same functionality as ac-call!, which currently has an io!
     ;; block.
     (let [tablename (throw (Exception. "fixme"))
           cookie (functional-sync-call
                   (GetCookie. (Str. (str tablename keyval)))
                   (ds :chan) 500)]
       {:k keyval
        :v cookie})))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-change!

(defmulti ds-change!
  "Change the record in the given table matching the given hash by
  primary key, setting it to the given hash."
  on-datastore)

(defn- update-query
  "Form an SQL query to set the given row, a hash, in the table
  backing the given durable map.  Return it wrapped in a structure
  where the query string is :query, the vector of values is :vals, and
  the time of the write is :time."
  [dmap row]
  (let [table (dmap :table)
        spec (dmap :spec)
        keycol (spec :key)
        cols (filter #(not= keycol %)
                     (keys (spec :cols)))
        keyval (row keycol)
        col-sets-str (apply str (interpose ", " (map #(str % " = ?")
                                                     (map name cols))))
        num-cols (count cols)
        query-str (str "UPDATE " table " SET " col-sets-str
                       " WHERE " (name keycol) " = ?")]
    {:dmap dmap
     :row row
     :query query-str
     :vals (valify-row row spec (concat cols (list keycol)))
     :time (tm)
     :keyval keyval}))

(defmethod ds-change! :sql
  [ds table row]
  (let [q (update-query table row)]
    (run-stmt! (ds :db)
               (q :query)
               (q :vals))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-add!

(defmulti ds-add!
  "Add a new record to the given table."
  on-datastore)

(defn- insert-query
  "Form an SQL query to insert the given row, a hash, into the table
  backing the given durable map.  Return it wrapped in a structure
  where the query string is :query, the vector of values is :vals, and
  the time of the write is :time."
  [dmap row]
  (let [table (dmap :table)
        spec (dmap :spec)
        cols (keys (spec :cols))
        keycol (spec :key)
        cols-str (apply str (interpose ", " (map name cols)))
        num-cols (count cols)
        val-?s (apply str (interpose ", " (take num-cols
                                                (repeat "?"))))
        query-str (str "INSERT INTO " table
                       " (" cols-str ") VALUES (" val-?s ")")]
    {:dmap dmap
     :row row
     :query query-str
     :vals (valify-row row spec cols)
     :time (tm)
     :required true
     :keyval (row keycol)}))

(defmethod ds-add! :sql
  [ds table row]
  (let [q (insert-query table row)]
    (run-stmt! (ds :db)
               (q :query)
               (q :vals))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-newtable!

(defmulti ds-newtable!
  "Add a new table with the given name and description to the given
  data store.  An optional boolean fourth parameter determines whether
  the pre-existence of a table with the same name should be allowed
  without an error.  (Default is to throw an error if the table
  already exists.)  Note that in some data stores, this function might
  always be a no-op."
  on-datastore)

(defn- column-format
  "Convert a spec map into a string suitable for putting between the
  parentheses of a CREATE TABLE statement."
  [spec]
  (let [cols  (spec :cols)
        name-type-seq  (map #(vector (name (first %))
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

(defn- create-table!
  "Create a table, given a name and a :spec map.  An optional third
  parameter, if true, specifies that the table should be created with
  CREATE TABLE IF NEW."
  ([db name spec]
     (create-table! name spec false))

  ([db name spec if-new]
     (io!)
     (let [cmd (if if-new "CREATE TABLE IF NOT EXISTS" "CREATE TABLE")
           cols (column-format spec)]
       (run-stmt! db
                  (apply str cmd " " name " (" cols ")")))))

(defmethod ds-newtable! :sql
  ([ds tablename tablespec]
     (ds-newtable! ds tablename tablespec false))
  ([ds tablename tablespec if-new]
     (create-table! (ds :db) tablename tablespec if-new)))

(defmethod ds-newtable! :dcookies
  ;; Nothing needs to be created.
  ;; TODO at least check that the table being created doesn't have an
  ;; unsupported schema.
  [_ _ _] nil)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-munge

(defmulti ds-munge
  "Mutate the given table name to something safe for use with the
  given datastore."
  on-datastore)

(defmethod ds-munge :sql
  [ds name]
  (apply str (take 16 (re-seq #"[a-zA-Z]" name))))

(defmethod ds-munge :dcookies
  [ds name]
  name)


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; default-spec

(defmulti default-spec
  "Return the default spec for any table on the given datastore, or
  nil if it does not have a default schema."
  on-datastore)

(defmethod default-spec :sql
  [ds]
  nil)

(defmethod default-spec :dcookies
  [ds]
  {:cols {:k [nil :dobj]
          :v [nil :dobj]}
   :key :k})


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; valid-spec

(defn valid-spec
  "Return true if the given spec is valid for the given datastore, or
  if the datastore imposes no special restrictions on table specs;
  otherwise false."
  [ds spec]
  (if-let [defspec (default-spec ds)]
    (= spec defspec)
    true))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;; ds-delete!

(defmulti ds-delete!
  "Delete the record from the given table with the given key,
  returning nil."
  on-datastore)

(defmethod ds-delete! :sql
  ([ds table keycol keyval]
     (let [tablename (table :table)
           spec (table :spec)
           keycol (or keycol (spec :key))
           db (ds :db)
           query-str (str "DELETE FROM " tablename " WHERE "
                          (name keycol) " = ?")
           vals (valify-row {keycol keyval} spec [keycol])]
       (run-stmt! db query-str vals)))
  ([ds table keyval]
     (ds-delete! ds table (get-pk table) keyval)))

(defmethod ds-delete! :dcookies
  ([ds table keycol keyval]
     )
  ([ds table keyval]
     ))

