;; Copyright (C) 2007-2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.


(ns distroverse.util
  (:import [java.net URL URI]
           [clojure.asm ClassWriter Opcodes]))


(defn define-java-class-from-bytecode
  ([classname bytecode]
     (.defineClass @clojure.lang.Compiler/LOADER classname bytecode nil)))

;;; Using ASM, define a Java class containing raw, unchecked
;;; operations not currently offered by Clojure.  This would be easier
;;; to write in Java, but I refuse to do that on principle.

(define-java-class-from-bytecode "distroverse.util.RawOps"
  (let
      [public Opcodes/ACC_PUBLIC
       static Opcodes/ACC_STATIC
       lload Opcodes/LLOAD
       iload Opcodes/ILOAD
       cw (ClassWriter. 0)]
    (.visit cw Opcodes/V1_5 (+ public Opcodes/ACC_SUPER)
            "distroverse/util/RawOps" nil "java/lang/Object" nil)
    (doto (.visitMethod cw public "<init>" "()V" nil nil)
      (.visitVarInsn Opcodes/ALOAD 0)
      (.visitMethodInsn Opcodes/INVOKESPECIAL "java/lang/Object"
                        "<init>" "()V")
      (.visitInsn Opcodes/RETURN)
      (.visitMaxs 1 1)
      (.visitEnd))
    (doseq [ [mname op] [ ["xor" Opcodes/LXOR]
                          ["and" Opcodes/LAND]
                          ["or"  Opcodes/LOR ] ] ]
      (doto (.visitMethod cw (+ public static)
                          mname "(JJ)J" nil nil)
        (.visitVarInsn lload 0)
        (.visitVarInsn lload 2)
        (.visitInsn op)
        (.visitInsn Opcodes/LRETURN)
        (.visitMaxs 4 4)
        (.visitEnd)))
    (doseq [ [mname op] [ ["shiftLeft"  Opcodes/LSHL]
                          ["shiftRight" Opcodes/LSHR] ] ]
      (doto (.visitMethod cw (+ public static)
                          mname "(JI)J" nil nil)
        (.visitVarInsn lload 0)
        (.visitVarInsn iload 2)
        (.visitInsn op)
        (.visitInsn Opcodes/LRETURN)
        (.visitMaxs 3 3)
        (.visitEnd)))
    (doseq [ [mname op] [ ["xor"        Opcodes/IXOR]
                          ["and"        Opcodes/IAND]
                          ["or"         Opcodes/IOR ]
                          ["shiftLeft"  Opcodes/ISHL]
                          ["shiftRight" Opcodes/ISHR] ] ]
      (doto (.visitMethod cw (+ public static)
                          mname "(II)I" nil nil)
        (.visitVarInsn iload 0)
        (.visitVarInsn iload 1)
        (.visitInsn op)
        (.visitInsn Opcodes/IRETURN)
        (.visitMaxs 2 2)
        (.visitEnd)))
    (doseq [intype ["J" "I" "S" "B"]
            outype ["J" "I" "S" "B"]]
      (when (not= intype outype)
        (let [mname ({"B" "byteCast"
                      "S" "shortCast"
                      "I" "intCast"
                      "J" "longCast"} outype)
              inlen  ({"J" 8, "I" 4, "S" 2, "B" 1} intype)
              outlen ({"J" 8, "I" 4, "S" 2, "B" 1} outype)]
          (let [mv (.visitMethod cw (+ public static) mname
                                 (str "(" intype ")" outype)
                                 nil nil)]
            (if (= intype "J")
              (.visitVarInsn mv lload 0)
              (.visitVarInsn mv iload 0))
            (when (and (= inlen 8)
                       (< outlen 8))
              (.visitInsn mv Opcodes/L2I))
            (when (and (> inlen 1)
                       (= outlen 1))
              (.visitInsn mv Opcodes/I2B))
            (when (and (> inlen 2)
                       (= outlen 2))
              (.visitInsn mv Opcodes/I2S))
            (when (and (< inlen 8)
                       (= outlen 8))
              (.visitInsn mv Opcodes/I2L))
            (if (= outype "J")
              (.visitInsn mv Opcodes/LRETURN)
              (.visitInsn mv Opcodes/IRETURN))
            (.visitMaxs mv
                        (if (or (= outlen 8)
                                (= inlen 8))
                          2 1)
                        (if (= inlen 8)
                          2 1))
            (.visitEnd mv)))))
    (.visitEnd cw)
    (.toByteArray cw)))

(import 'distroverse.util.RawOps)

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
           `(. distroverse.util.RawOps ~'~(symbol (str primtype "Cast"))
               ~~'n)))))

(doseq [t ["byte" "short" "int" "long"]]
  (eval `(defmacro-raw-cast ~t)))

(defmacro defmacro-raw-op
  "Defines a macro for performing an unchecked numeric primitive
  operation using RawOps"
  ([article op mname]
     `(defmacro ~(symbol (str "raw-" op))
        ~(str "Performas " article " " op " on numeric primitives")
        ([~'a ~'b]
           `(. distroverse.util.RawOps ~'~mname
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

