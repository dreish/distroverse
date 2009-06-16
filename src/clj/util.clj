
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

(ns util
  (:use clojure.contrib.def))

(import '(org.distroverse.dvtp Real))

(defn queue
  "Creates a new queue containing the args."
  ([]
     clojure.lang.PersistentQueue/EMPTY)
  ([& args]
     (loop [q (queue)
            a args]
       (if a
         (recur (conj q (first a)) (next a))
         q))))

(defn cinc
  "inc for characters"
  [#^Character c]
  (-> c int inc char))

(defn crange
  "Given two characters, returns a character range.  Like range, the
  end is not included."
  [#^Character begin #^Character end]
  (lazy-seq
    (if (not= begin end)
      (cons begin
            (crange (cinc begin) end)))))

(let [nybs (vec (concat (crange \0 (cinc \9))
                        (crange \a (cinc \f))))]
  (defn hex-encode-bytes
    "Takes a sequence of bytes and returns a sequence of hex
    characters."
    [bytes]
    (mapcat #(list (nybs (quot (bit-and % 240) 16))
                   (nybs (bit-and % 15)))
            bytes)))

(defn inc-in
  "Return the given hash with the given field incremented."
  [h field]
  (assoc h
    field (inc (h field))))

(defmacro +>
  "Threads the expr through the forms. Inserts x as the
  second item in the first form, making a list of it if it is not a
  list already. If there are more forms, inserts the first form as the
  second item in second form, etc.  For any form containing an
  underscore, the preceding form is inserted at that position."
  ([x form]
     (if (and (seq? form) (not= (first form) 'fn*))
       (if (some #(= % '_) form)
         `(~@(take-while #(not= % '_) form)
           ~x
           ~@(next (drop-while #(not= % '_) form)))
         `(~(first form) ~x ~@(next form)))
       (list form x)))
  ([x form & more]
     `(+> (+> ~x ~form) ~@more)))

(defn min-by
  "Return the least member of the sequence s by comparing the results
  of applying f to the members of s.  Best for cheap functions; f will
  be called twice on each element in s."
  [f s]
  ; TODO Could cut the calls to f in half with a loop/recur.
  (reduce #(if (neg? (compare (f %1) (f %2)))
             %1
             %2)
          s))

(defn dissoc-in
  [m k & ks]
  (if ks
    (assoc m k (apply dissoc-in (get m k) ks))
    (dissoc m k)))

(let [o (Object.)]
  (defn exists?
    "Does the given key exist in the given hash?"
    ([k h]
       (not= o (h k o)))))

(defn rests
  "Returns a lazy sequence of successive rests of coll, beginning with
  a seq of the entire collection and ending with a seq of count 1."
  [coll]
  (take-while seq (iterate rest coll)))

(defn do-or
  "(or) as a function, evaluating all its arguments, but returning the
  first true one just as (or) does."
  [& args]
  (reduce #(or %1 %2) args))

(defn gmt-time-string
  []
  (. (java.util.Date.) toGMTString))

(defn apply-lambda
  "Returns a function of no arguments that, when called, will return
  the result of applying the function named by (first s) to (rest s).
  E.g., ((apply-lambda '(+ 1 2))) => 3."
  [s]
  (fn [] (apply (resolve (first s))
                (rest s))))

(defn firstarg
  "Return the first of any arbitrary number of arguments."
  [x & _]
  x)

(defn tm
  "Return a time value such that each call to (tm) returns a number
  greater than or equal to all numbers previously returned.  The units
  of the time value are not specified."
  []
  (System/currentTimeMillis))

(defn now
  "Return the current time as a Real, the format that is used by DVTP,
  e.g., as the argument to MoveSeq.transformAt()."
  []
  (Real. (BigDecimal. (BigInteger/valueOf (System/currentTimeMillis))
                      3)))
