;; Copyright (C) 2007-2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.


(ns distroverse.protocol)

(defn ulong-to-bytes
  "Given a nonnegative integer, return a seq of bytes"
  ([n]
     (lazy-seq
      (if (neg? n)
        (throw (Exception. (str "ulong-to-bytes arg < 0: " n))))
      (if (< n 128)
        (list n)
        (cons (bit-or 128 (bit-and n 127))
              (ulong-to-bytes (bit-shift-right n 7)))))))

(defn return-pair
  "Object returnable from a function containing two return values"
  ;; This looks stupid, and might actually be stupid, but I want to
  ;; keep open the possibility of someday doing something faster here.
  ([x y]
     [x y]))

(defn pair-first
  "First item of a pair returned by return-pair"
  ([p]
     (p 0)))

(defn pair-second
  "Second item of a pair returned by return-pair"
  ([p]
     (p 1)))

(defn bytes-to-ulong
  "Given a seq of bytes, return a nonnegative integer and the remaining
  unconsumed bytes (using return-pair)"
  ([bs]
     (loop [n     0
            order 0
            bs    bs]
       (let [byte (first bs)]
         (if (< byte 128)
           (return-pair (+ n (bit-shift-left byte order))
                        (rest bs))
           (recur (+ n (bit-shift-left (bit-and byte 127)
                                       order))
                  (+ order 7)
                  (rest bs)))))))

(defmacro defmessage
  "XXX would be nice to have a stub defmacro-XXX like defn-XXX"
  ([& forms]
     nil))

(defmessage ulong
  "Nonnegative arbitrarily large integer packed 7 bits per byte"
  :class 0
  :encode ulong-to-bytes
  :decode bytes-to-ulong)

(defn string-to-bytes
  "Given a string, return a seq of bytes"
  ([s]
     (lazy-seq
      (let [ba (.getBytes s "UTF-8")
            l  (alength ba)]
        (lazy-cat (ulong-to-bytes l)
                  (seq ba))))))

(defmacro consume-from
  "Take bytes from byte sequence bs, setting x to the value consumed
  using function f, and evaluate body in that environment"
  ([bs x f & body]
     `(let [pair# (~f ~bs)
            ~x (pair-first pair#)
            ~bs (pair-second pair#)]
        ~@body)))

(defn bytes-to-string
  "Given a seq of bytes, return a string and the remaining unconsumed
  bytes (using return-pair)"
  ([bs]
     ;; XXX use consume-from here
     (let [len-pair (bytes-to-ulong bs)
           len (pair-first len-pair)
           bs (pair-second len-pair)]
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
  return a seq of bytes"
  ([n i]
     (lazy-seq
      (if (zero? n)
        (do
          (assert (or (zero? i)
                      (= -1 i)))
          ())
        (cons (.byteValue i)
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
  "Given a seq of bytes and a number of bytes to parse, return a
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
  "Given a float, return a seq of bytes"
  ([f]
     (lazy-seq
      (let [i (Float/floatToIntBits f)]
        (fixnum-to-bytes 4 i)))))

(defn bytes-to-float
  "Given a seq of bytes, return a float and the remaining unconsumed
  bytes (using return-pair)"
  ([bs]
     (consume-from bs i (partial bytes-to-fixnum 4)
       (return-pair (Float/intBitsToFloat i)
                    bs))))

(defn double-to-bytes
  "Given a double, return a seq of bytes"
  ([d]
     (lazy-seq
      (let [l (Double/doubleToLongBits d)]
        (fixnum-to-bytes 8 l)))))

(defn bytes-to-double
  "Given a seq of bytes, return a double and the remaining unconsumed
  bytes (using return-pair)"
  ([bs]
     (consume-from bs l (partial bytes-to-fixnum 8)
       (return-pair (Double/longBitsToDouble l)
                    bs))))
