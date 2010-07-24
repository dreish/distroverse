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
  "Given a nonnegative integer, returns a seq of bytes"
  ([n]
     (lazy-seq
      (if (neg? n)
        (throw (Exception. (str "ulong-to-bytes arg < 0: " n))))
      (if (< n 128)
        (list n)
        (cons (bit-or 128 (bit-and n 127))
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
         (if (< byte 128)
           (return-pair (+ n (bit-shift-left byte order))
                        (rest bs))
           (recur (+ n (bit-shift-left (bit-and byte 127)
                                       order))
                  (+ order 7)
                  (rest bs)))))))

(def *class-to-message* {})

(def *message-data* {})

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
        (cons (.byteValue i)           ; (cast without overflow check)
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
