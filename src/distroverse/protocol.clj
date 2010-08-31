;; Copyright (C) 2007-2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.


(ns distroverse.protocol
  (:use (distroverse util)
        (clojure.contrib def)))

(defn ulong-to-bytes
  "Given a nonnegative integer, returns a seq of bytes"
  ([n]
     (lazy-seq
      (if (neg? n)
        (throw (Exception. (str "ulong-to-bytes arg < 0: " n))))
      (if (< n 128)
        (list n)
        (cons (unchecked-byte (bit-or 128 (bit-and n 127)))
              (ulong-to-bytes (bit-shift-right n 7)))))))

(defn return-pair
  "Returns an object returnable from a function containing two return
  values"
  ;; This looks stupid, and might actually be stupid, but I want to
  ;; keep open the possibility of someday doing something faster here.
  ([x y]
     [x y]))

(defn pair-first
  "Returns the first item of a pair returned by return-pair"
  ([p]
     (p 0)))

(defn pair-second
  "Returns the second item of a pair returned by return-pair"
  ([p]
     (p 1)))

(defn bytes-to-ulong
  "Given a seq of bytes, returns a nonnegative integer and the remaining
  unconsumed bytes (using return-pair)"
  ([bs]
     (loop [n     0
            order 0
            bs    bs]
       (let [byte (first bs)]
         (if (>= byte 0)
           (return-pair (+ n (bit-shift-left byte order))
                        (rest bs))
           (recur (+ n (bit-shift-left (bit-and byte 127)
                                       order))
                  (+ order 7)
                  (rest bs)))))))

(defvar *class-to-message* {}
  "Maps a numeric class to a message class keyword (e.g., :float)")

(defvar *message-data* {}
  "Maps a message class (e.g., :float) to a hash defining that class")

(defmacro defmessage
  ; XXX would be nice to have a stub defmacro-XXX like defn-XXX
  "Defines a message class"
  ([mname & forms]
     (let [has-docstring? (string? (first forms))
           docstring      (if has-docstring? (first forms) nil)
           forms          (if has-docstring? (rest forms) forms)
           options        (apply hash-map forms)
           mname-keyword  (keyword mname)]
       (when-not (options :class)
         (throw (Exception. "defmessage requires at least a :class")))
       `(do
          (def *class-to-message*
               (assoc *class-to-message*
                 ~(options :class)
                 ~mname-keyword))
          (def *message-data*
               (assoc *message-data*
                 ~mname-keyword
                 ~options))))))

(defmessage ulong
  "Nonnegative arbitrarily large integer packed 7 bits per byte"
  :class 0
  :encode ulong-to-bytes
  :decode bytes-to-ulong)

(defn string-to-bytes
  "Given a string, returns a seq of bytes"
  ([s]
     (lazy-seq
      (let [ba (.getBytes s "UTF-8")
            l  (alength ba)]
        (lazy-cat (ulong-to-bytes l)
                  (seq ba))))))

(defmacro consume-from
  "Takes bytes from byte sequence bs, setting x to the value consumed
  using function f, and evaluates body in that environment"
  ([bs x f & body]
     `(let [pair# (~f ~bs)
            ~x (pair-first pair#)
            ~bs (pair-second pair#)]
        ~@body)))

(defn bytes-to-string
  "Given a seq of bytes, returns a string and the remaining unconsumed
  bytes (using return-pair)"
  ([bs]
     (consume-from bs len bytes-to-ulong
       (return-pair (String. (byte-array (take len bs))
                             "UTF-8")
                    (drop len bs)))))

(defmessage string
  "Length-prefixed UTF-8 encoded string"
  :class 1
  :encode string-to-bytes
  :decode bytes-to-string)

(defn fixnum-to-bytes
  "Given a signed integer and a number of bytes to split it into,
  returns a seq of bytes"
  ([n i]
     (lazy-seq
      (if (zero? n)
        (do
          (assert (or (zero? i)
                      (= -1 i)))
          ())
        (cons (unchecked-byte i)
              (fixnum-to-bytes (dec n)
                               (bit-shift-right i 8)))))))

(defn byte-to-ubyte
  "Takes a signed byte (-128 to 127) and returns an unsigned byte (0
  to 255), folding negative numbers to the range 128-255"
  ([b]
     (if (neg? b)
       (+ b 256)
       b)))

(defn bytes-to-fixnum
  "Given a seq of bytes and a number of bytes to parse, returns a
  signed integer and the remaining unconsumed bytes (using
  return-pair)"
  ([n bs]
     (if (zero? n)
       (return-pair 0 bs))
     (loop [n    n
            bs   bs
            mult 1
            i    0]
       (let [byte (first bs)]
         (if (= n 1)
           (return-pair (+ i (* mult byte))
                        (rest bs))
           (recur (dec n)
                  (rest bs)
                  (* mult 256)
                  (+ i (* mult (byte-to-ubyte byte)))))))))

(defn float-to-bytes
  "Given a float, returns a seq of bytes"
  ([f]
     (lazy-seq
      (let [i (Float/floatToIntBits f)]
        (fixnum-to-bytes 4 i)))))

(defn bytes-to-float
  "Given a seq of bytes, returns a float and the remaining unconsumed
  bytes (using return-pair)"
  ([bs]
     (consume-from bs i (partial bytes-to-fixnum 4)
       (return-pair (Float/intBitsToFloat i)
                    bs))))

(defmessage float
  "Java Float"
  :class 2
  :encode float-to-bytes
  :decode bytes-to-float)

(defn double-to-bytes
  "Given a double, returns a seq of bytes"
  ([d]
     (lazy-seq
      (let [l (Double/doubleToLongBits d)]
        (fixnum-to-bytes 8 l)))))

(defn bytes-to-double
  "Given a seq of bytes, returns a double and the remaining unconsumed
  bytes (using return-pair)"
  ([bs]
     (consume-from bs l (partial bytes-to-fixnum 8)
       (return-pair (Double/longBitsToDouble l)
                    bs))))

(defmessage double
  "Java Double"
  :class 3
  :encode double-to-bytes
  :decode bytes-to-double)

(defn class-to-bytes-fn
  "Given a message class (e.g., :float), returns the function that
  takes an object of that type and returns a seq of bytes encoding
  that object"
  ([c]
     (let [c-data (*message-data* c)]
       (c-data :encode))))

(defn bytes-to-class-fn
  "Given a message class (e.g., :float), returns the function that
  takes a seq of bytes and returns an object of that type and the
  remaining unconsumed bytes"
  ([c]
     (let [c-data (*message-data* c)]
       (c-data :decode))))

(defn array-to-bytes
  "Given a message class (e.g., :float) and a seq of objects, returns
  a seq of bytes *without* a class tag or count prefix"
  ([c os]
     (let [converter (class-to-bytes-fn c)]
       (mapcat converter os))))

(defn bytes-to-array
  "Given a message class (e.g., :float), a number of objects to read,
  and a seq of bytes, returns a vector of objects of that type and the
  remaining unconsumed bytes"
  ([c n bs]
     (let [decoder (bytes-to-class-fn c)]
       (loop [n   n
              ret []
              bs  bs]
         (if (zero? n)
           (return-pair ret bs)
           (consume-from bs o decoder
             (recur (dec n)
                    (conj ret o)
                    bs)))))))

(defmessage url
  "Length-prefixed UTF-8 encoded string containing a URL.  Sent from
  client to envoy, it is a command to go to this URL.  Sent from envoy
  to client, it is informational, indicating that the user has
  navigated to the given URL."
  :class 4
  :encode string-to-bytes
  :decode bytes-to-string)



(defn message-type
  "Returns the type of the given message"
  ([m]
     (:type m)))

