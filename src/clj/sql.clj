
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

(ns sql
  (:use clojure.contrib.def))


(defn fill-placeholders
  "Returns a Statement, which the caller is responsible for closing."
  [conn query values]
  (let [statement (.prepareStatement conn query)]
    (doseq [[index value] (map vector
                               (iterate inc 1)
                               values)]
        (.setObject statement index value))
    statement))

(defn num-columns
  "Number of columns in the given ResultSet."
  [rs]
  (.. rs getMetaData getColumnCount))

(defn rs-row
  [rs n-cols]
  (doall (map #(.getObject rs (int %))
              (range 1 (inc n-cols)))))

(defn rs-seq
  "Return a sequence of results.  CAUTION: probably not safe to lazily
  evaluate this after closing an rs."
  [rs n-cols]
  (if (.next rs)
    (lazy-cons (rs-row rs n-cols)
               (if (.next rs)
                 (rs-seq rs n-cols)))))

(defn rs-vec
  "Return a vector of results."
  [rs]
  (vec (rs-seq rs (num-columns rs))))

(defn column-names
  [rs]
  (let [rsmeta (.getMetaData rs)]
    (vec (map #(.getColumnName rsmeta %)
              (range 1 (inc (.getColumnCount rsmeta)))))))

(defn get-query
  "Run a query with results, and return the results as a hash in
  which :rows is a vector of row vectors, and :colnames is a vector of
  column names.  The optional third argument provides values to
  substitute for placeholders."
  ([conn q]
     (get-query conn q []))

  ([conn q pvals]
     (with-open [s (fill-placeholders conn q pvals)]
         (let [rs (.executeQuery s)
               colnames (column-names rs)
               rsv (rs-vec rs)]
           {:colnames colnames
            :rows rsv}))))

(defn run-stmt!
  "Run a statement, returning the number of rows affected for INSERT,
  UPDATE, or DELETE statements, or 0 for statements that return
  nothing.  The optional third argument provides values to substitute
  for placeholders."
  ([conn q]
     (run-stmt! conn q []))

  ([conn q pvals]
     (prn (symbol "Running query:") q (symbol "::") pvals)
     (with-open [s (fill-placeholders conn q pvals)]
         (.executeUpdate s))))
