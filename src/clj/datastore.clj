
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
        dcookies
        dvtp-lib))

; Simple interface for an arbitrary, possibly asynchronous data
; storage backend, preferably supporting simultaneous atomic
; modifications of multiple records.

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

(defmulti ds-get!
  "Get the record from the given table with the given key, returning a
  hash.  If no keycol is given, use the table's primary key."
  on-datastore)

(defmethod ds-get! :sql
  ([ds tablename keycol keyval]
     ;; XXX
     )
  ([ds tablename keyval]
     (ds-get ds tablename (get-pk ds tablename) keyval)))


(defmethod ds-get! :dcookies
  ([ds tablename keycol keyval]
     (if (not= :k keycol)
       (throw (Exception. (str "datastore :dcookies does not support key"
                               " columns other than :k")))
       (ds-get ds tablename keyval)))
  ([ds tablename keyval]
     ;; XXX this is going to require a synchronous wrapper around the
     ;; same functionality as ac-call!, which currently has an io!
     ;; block.
     (let [cookie (functional-sync-call
                   (GetCookie. (Str. tablename keyval))
                   (ds :chan) 500)]
       {:k keyval
        :v cookie})))


(defmulti ds-change!
  "Change the record in the given table matching the given hash by
  primary key, setting it to the given hash."
  on-datastore)

(defmethod ds-change! :sql
  [ds tablename row]
  ;; XXX
  )


(defmulti ds-add!
  "Add a new record to the given table."
  on-datastore)

(defmethod ds-add! :sql
  [ds tablename row]
  ;; XXX
  )


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
  ([name spec]
     (create-table! name spec false))

  ([name spec if-new]
     (io!)
     (let [cmd (if if-new "CREATE TABLE IF NOT EXISTS" "CREATE TABLE")
           cols (column-format spec)]
       (run-stmt! @*dm-db*
                  (apply str cmd " " name " (" cols ")")))))

(defmethod ds-newtable! :sql
  ([ds tablename tablespec]
     (ds-newtable ds tablename tablespec false))
  ([ds tablename tablespec if-new]
     (create-table! tablename tablespec if-new)))

(defmethod ds-newtable! :dcookies
  ;; Nothing needs to be created.
  ;; TODO at least check that the table being created doesn't have an
  ;; unsupported schema.
  [_ _ _] nil)


(defmulti ds-delete!
  "Delete the record from the given table with the given key,
  returning nil."
  on-datastore)

(defmethod ds-delete! :sql
  ([ds tablename keycol keyval]
     ;; XXX
     )
  ([ds tablename keyval]
     (ds-delete ds tablename (get-pk ds tablename) keyval)))

