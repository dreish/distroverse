;; Copyright (C) 2007-2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.


(ns distroverse.util
  (:use [distroverse.assem raw-ops]
        [cantor])
  (:import [java.net URL URI]
           [distroverse.assem RawOps]))

(defn unchecked-byte
  "Coerces to byte without checking for numeric overflow"
  ([^Number x]
     (.byteValue x)))

(defmacro defmacro-raw-cast
  "Defines a macro for performing an unchecked numeric primitive cast
  using RawOps"
  ([primtype]
     `(defmacro ~(symbol (str "raw-" primtype))
        ~(str "Coerces to " primtype
              " without checking for numeric overflow")
        ([~'n]
           `(. distroverse.assem.RawOps ~'~(symbol (str primtype "Cast"))
               ~~'n)))))

(doseq [t ["byte" "short" "int" "long"]]
  (eval `(defmacro-raw-cast ~t)))

(defmacro defmacro-raw-op
  "Defines a macro for performing an unchecked numeric primitive
  operation using RawOps"
  ([article op mname]
     `(defmacro ~(symbol (str "raw-" op))
        ~(str "Performs " article " " op " on numeric primitives")
        ([~'a ~'b]
           `(. distroverse.assem.RawOps ~'~mname
               ~~'a ~~'b)))))

(doseq [ [article op mname] [ ["an" "xor" 'xor]
                              ["an" "and" 'and]
                              ["an" "or"  'or]
                              ["a" "shift-left" 'shiftLeft]
                              ["a" "shift-right" 'shiftRight] ] ]
  (eval `(defmacro-raw-op ~article ~op ~mname)))

(defn get-host
  "Returns the host portion of the given URI"
  ([uri]
     (.getHost (URI. uri))))

(defn default-port-for-scheme
  "Returns the default port for schemes defined by Distroverse"
  ([scheme]
     ( {"dvtp" 1808}
       scheme )))

(defn get-port
  "Returns the port for the given URI, looking up the default port for
  the protocol associated with the URI if it does not specify a port"
  ([uri-str]
     (let [uri (URI. uri-str)
           p (.getPort uri)
           scheme (.getScheme uri)]
       (if (= -1 p)
         (if-let [p (default-port-for-scheme scheme)]
           p
           (.getDefaultPort (URL. uri-str)))
         p))))

(defn get-and-set!
  "Atomically sets the value of the given atom and returns its
  previous value"
  ([atom newval]
     (loop []
       (let [oldval @atom]
         (if (compare-and-set! atom oldval newval)
           oldval
           (recur))))))

(defn is-to-byteseq
  "Takes ownership of an InputStream and returns a seq of bytes"
  ([is]
     (lazy-seq
      (let [b (.read is)]
        (when (not= b -1)
          (cons b (is-to-byteseq is)))))))

(defn forkexec
  "Runs the given command line in a subprocess and returns that
  Process object"
  ([cmd]
     (.exec (Runtime/getRuntime)
            cmd)))

(defrecord Pair [a b])

(defn pair-first
  ([^Pair p]
     (.a p)))

(defn pair-second
  ([^Pair p]
     (.b p)))


(defn stream-send!
  "Sends to the given stream the given seq of bytes."
  ([os bs]
     (doseq [b bs]
       (.write os (int b)))))

(def Infinity Double/POSITIVE_INFINITY)

(def -Infinity Double/NEGATIVE_INFINITY)

(def ident-quat {:w 1.0, :x 0.0, :y 0.0, :z 0.0})

(defn normalize-quat
  "Returns the unit quaterion corresponding to the given quaterion"
  ([q]
     (let [{w :w, x :x, y :y, z :z} q
           n (Math/sqrt (+ (* w w) (* x x) (* y y) (* z z)))]
       {:w (/ w n)
        :x (/ x n)
        :y (/ y n)
        :z (/ z n)})))

(defn quat-to-angle-axis
  "Returns an angle-axis rotation in (theta, x, y, z) form for the
  given quaternion, which must be normalized"
  ([q]
     (let [w (:w q)
           x (:x q)
           y (:y q)
           z (:z q)
           scale (Math/sqrt (+ (* x x)
                               (* y y)
                               (* z z)))
           angle (* 2.0 (Math/acos w))]
       (if (zero? scale)
         [0.0 1.0 0.0 0.0]
         [angle
          (/ x scale)
          (/ y scale)
          (/ z scale)]))))

(defn angle-axis-to-quat
  "Returns a quaternion for the given angle-axis rotation arguments"
  ([theta x y z]
     (let [half-theta (/ theta 2.0)
           norm-scalar (/ (Math/sqrt (+ (* x x) (* y y) (* z z))))
           sin-half-theta (Math/sin half-theta)]
       {:w (Math/cos half-theta)
        :x (* norm-scalar x sin-half-theta)
        :y (* norm-scalar y sin-half-theta)
        :z (* norm-scalar z sin-half-theta)})))


(defn pos-to-moveseq
  "Returns a sequence of one move that is stationary at the given
  coordinates."
  ([x y z]
     [{:poly [{:move [x y z]
               :rot ident-quat}]
       :sines []
       :timebase 0
       :dur Infinity}]))

(def zero-move-element
     {:move [0.0 0.0 0.0]
      :rot ident-quat})


(defn vec-mul
  "Returns the given vector multiplied by the given scalar"
  ([v n]
     (vec (map (partial * n)
               v))))

(defn vec-add
  "Returns the sum of the two given vectors"
  ([a b]
     (vec (map + a b))))

(defn quat-pow
  "Returns the given quaternion raised to the given power"
  ([q n]
     (let [ [theta x y z] (quat-to-angle-axis (normalize-quat q)) ]
       (angle-axis-to-quat (* theta n) x y z))))

(defn quat-mul
  "Returns the product of the two given unit quaternions"
  ([q1 q2]
     (let [{w1 :w, x1 :x, y1 :y, z1 :z} q1
           {w2 :w, x2 :x, y2 :y, z2 :z} q2]
       {:w (- (* w1 w2) (* x1 x2) (* y1 y2) (* z1 z2))
        :x (+ (* w1 x2) (* x1 w2) (* y1 z2) (- (* z1 y2)))
        :y (+ (* w1 y2) (* y1 w2) (* z1 x2) (- (* x1 z2)))
        :z (+ (* w1 z2) (* z1 w2) (* x1 y2) (- (* y1 x2)))})))

(defn mul-move-element
  "Returns the given move-element multiplied by the given scalar"
  ([mel n]
     (let [move (:move mel)
           rot (:rot mel)]
       {:move (vec-mul move n)
        :rot (quat-pow rot n)}
       ;; XXX
       )))

(defn add-move-element
  "Returns the sum of the two given movements"
  ([a b]
     {:move (vec-add (:move a) (:move b))
      :rot (quat-mul (:rot a) (:rot b))}))

(defn eval-move-poly
  "Returns the move-element value of the given move polynomial at the
  given time"
  ([t mp]
     (loop [a 1.0
            mp mp
            ret zero-move-element]
       (if (seq mp)
         (recur (* a t)
                (rest mp)
                (-> (first mp)
                    (mul-move-element a)
                    (add-move-element ret)))
         ret))))

(defn eval-move-sines
  "Returns the move-element value of the given series of move sines at
  the given time"
  ([t ms]
     (loop [ms ms
            ret zero-move-element]
       (if (seq ms)
         (let [m (first ms)
               theta (+ (:offset m)
                        (* 2.0 Math/PI (/ t (:period m))))
               a (Math/sin theta)]
           (recur (rest ms)
                  (-> (:mel m)
                      (mul-move-element a)
                      (add-move-element ret))))
         ret))))

(defn eval-move
  "Returns the move-element value of the given move function at the
  given time"
  ([t m]
     (let [t (- t (:timebase m))]
       (add-move-element (eval-move-poly t (:poly m))
                         (eval-move-sines t (:sines m))))))

(defn current-pos
  "Takes an elapsed time and a seq of moves and returns the current
  position."
  ([t ms]
     (loop [t t
            ms (cycle ms)
            i 0]
       (when (> i 100000)
         (throw (Exception. "Maximum recursion in moveseq reached")))
       (let [m (first ms)
             dur (:dur m)]
         (if (> t dur)
           (recur (- t dur)
                  (rest ms)
                  (inc i))
           (eval-move t m))))))

