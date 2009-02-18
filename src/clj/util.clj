
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

(ns util
  (:use clojure.contrib.def))

(defn queue
  "Creates a new queue containing the args."
  ([]
     clojure.lang.PersistentQueue/EMPTY)
  ([& args]
     (loop [q (queue)
            a args]
       (if a
         (recur (conj q (first a)) (rest a))
         q))))

(defn cinc
  "inc for characters"
  [#^Character c]
  (-> c int inc char))

(defn crange
  "Given two characters, returns a character range.  Like range, the
  end is not included."
  [#^Character begin #^Character end]
  (if (not= begin end)
    (lazy-cons begin
               (crange (cinc begin) end))))

(let [nybs (vec (concat (crange \0 (cinc \9))
                        (crange \a (cinc \f))))]
  (defn hex-encode-bytes
    "Takes a sequence of bytes and returns a sequence of hex
    characters."
    [bytes]
    (if bytes
      (let [fb (first bytes)
            high-nyb (quot (bit-and fb 240) 16)
            low-nyb  (bit-and fb 15)]
        (cons (nybs high-nyb)
              (lazy-cons (nybs low-nyb)
                         (hex-encode-bytes (rest bytes))))))))

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
           ~@(rest (drop-while #(not= % '_) form)))
         `(~(first form) ~x ~@(rest form)))
       (list form x)))
  ([x form & more]
     `(+> (+> ~x ~form) ~@more)))

(defn min-by
  "Return the least member of the sequence s by comparing the results
  of applying f to the members of s."
  [f s]
  ; TODO Could cut the calls to f in half with a loop/recur.
  (reduce #(if (neg? (compare (f %1) (f %2)))
             %1
             %2)
          s))

