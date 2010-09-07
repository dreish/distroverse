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
        (clojure.contrib def)
        (clojure set)))

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

(defvar class-to-message {}
  "Maps a numeric class to a message class keyword (e.g., :float)")

(defvar message-data {}
  "Maps a message class (e.g., :float) to a hash defining that class")

(defmacro defmessage
  "Defines a message class"
  ([mname & forms]
     (let [has-docstring? (string? (first forms))
           docstring      (if has-docstring? (first forms) nil)
           forms          (if has-docstring? (rest forms) forms)
           options        (apply hash-map forms)
           mname-keyword  (keyword mname)]
       (when-not (contains? options :class)
         (throw (Exception. "defmessage requires at least a :class")))
       `(do
          (def class-to-message
               (assoc class-to-message
                 ~(options :class)
                 ~mname-keyword))
          (def message-data
               (assoc message-data
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
     (let [c-data (message-data c)]
       (c-data :encode))))

(defn bytes-to-class-fn
  "Given a message class (e.g., :float), returns the function that
  takes a seq of bytes and returns an object of that type and the
  remaining unconsumed bytes"
  ([c]
     (let [c-data (message-data c)]
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

(defmessage dvertex
  "Three doubles representing x-, y-, and z-coordinates"
  :class 5
  :encode (partial array-to-bytes :double)
  :decode (partial bytes-to-array :double 3))

(defn bool-to-bytes
  "Given a boolean, returns a seq of one byte"
  ([b]
     (if b
       (list (byte 1))
       (list (byte 0)))))

(defn bytes-to-bool
  "Given a seq of bytes, returns a boolean and the remaining
  unconsumed bytes (using return-pair)"
  ([bs]
     (let [bcode (first bs)
           b (cond (= 0 bcode) false
                   (= 1 bcode) true
                   :else (throw (Exception. "bool not 0 or 1")))]
       (return-pair b (rest bs)))))

(let [tripatterns
        {0 :triangle-fan}
      tripattern-codes
        (map-invert tripatterns)]
  (defn tripattern-to-bytes
    ([st]
       (ulong-to-bytes (tripattern-codes st))))
  (defn bytes-to-tripattern
    ([bs]
       (consume-from bs tripattern-code bytes-to-ulong
         (return-pair (tripatterns tripattern-code)
                      bs)))))

(defmessage tripattern
  "Code identifying .  Does not have a class number."
  :class nil
  :encode tripattern-to-bytes
  :decode bytes-to-tripattern)

(defn map-encoders
  "Given a symbol name and a defcodec spec, returns a seq of the
  encoder calls needed to encode a variable with the given symbol as
  its name, and of the specified message type"
  ([sym spec]
     (lazy-seq
      (when (seq spec)
        (let [nam (first spec)
              typ (second spec)]
          (if (vector? typ)
            (list* `(ulong-to-bytes (count (~sym ~nam)))
                   `(array-to-bytes ~(first typ)
                                    (~sym ~nam))
                   (map-encoders sym (rest (rest spec))))
            (cons `( (class-to-bytes-fn ~typ)
                     (~sym ~nam) )
                  (map-encoders sym (rest (rest spec))))))))))

(defn chain-consumers
  "Returns code to consume-from 'bs the fields defined in spec, and
  then return-pair a map containing those fields and the remaining
  unconsumed 'bs"
  ([spec]
     (chain-consumers spec spec))
  ([spec rem]
     (lazy-seq
      (if (seq rem)
        (let [nam (first rem)
              typ (second rem)]
          (if (vector? typ)
            (let [num-items-sym (gensym "__num_items")
                  typ (first typ)]
              (list `consume-from 'bs num-items-sym `bytes-to-ulong
                    (list `consume-from 'bs (symbol (name nam))
                          `(partial bytes-to-array ~typ ~num-items-sym)
                          (chain-consumers spec (rest (rest rem))))))
            (list `consume-from 'bs (symbol (name nam))
                  `(bytes-to-class-fn ~typ)
                  (chain-consumers spec (rest (rest rem))))))
        `(return-pair ~(apply hash-map
                              (mapcat #(list % (symbol (name %)))
                                      (take-nth 2 spec)))
                      ~'bs)))))

(defmacro defcodec
  "Defines an x-to-bytes function and a bytes-to-x function for a
  message type that is strictly a composition of other message types"
  ([t desc spec]
     (let [encoder-name (symbol (str t "-to-bytes"))
           decoder-name (symbol (str "bytes-to-" t))]
       `(do
          (defn ~encoder-name
            ~(str "Given " desc ", returns a seq of bytes")
            ([~'msg]
               (lazy-cat ~@(map-encoders 'msg spec))))
          (defn ~decoder-name
            ~(str "Given a seq of bytes, returns " desc " and the"
                  " remaining unconsumed bytes (using return-pair)")
            ([~'bs]
               ~(chain-consumers spec)))))))

(defcodec shape
  "a shape with a :tripat, :color, and :verts"
  [:tripat :tripattern
   :color :dvertex
   :verts [:dvertex]])

(defmessage shape
  "Triangle pattern, array of vertices, and color"
  :class 6
  :encode shape-to-bytes
  :decode bytes-to-shape)

(defcodec add-object
  "an add-object command with a numeric ID, parent ID, position, and
  zero or more shapes"
  [:id :ulong
   :pid :ulong
   :pos :vertex
   :shapes [:shape]])

(defmessage add-object
  "Numeric ID, numeric ID of parent, position, and optional shapes"
  :class 7
  :encode add-object-to-bytes
  :decode bytes-to-add-object)

(defn message-type
  "Returns the type of the given message"
  ([m]
     (:type m)))

(defn message
  "Returns a new message of type t, with value v"
  ([t v]
     {:type t
      :value v}))

(defn bytes-to-message
  "Takes a seq of bytes and returns a message and the remaining
  unconsumed bytes (using return-pair)"
  ([bs]
     (consume-from bs mclass-num bytes-to-ulong
       (let [mclass (class-to-message mclass-num)
             mdata (message-data mclass)
             decoder (mdata :decode)]
         (consume-from bs msg decoder
           (return-pair (message mclass msg)
                        bs))))))

(defn bytes-to-messages
  "Takes a seq of bytes and returns a seq of messages.  Throws an
  exception if the byte seq ends in the middle of a message."
  ([bs]
     (lazy-seq
      (when (seq bs)
        (consume-from bs message bytes-to-message
          (cons message
                (bytes-to-messages bs)))))))


