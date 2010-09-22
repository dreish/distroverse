;; Copyright (C) 2007-2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.assem.raw-ops
  (:use [clojure.contrib.duck-streams :only [make-parents copy
                                             to-byte-array]])
  (:import [clojure.asm ClassWriter Opcodes]
           [java.io File FileOutputStream]))

;;; Using ASM, define a Java class containing raw, unchecked
;;; operations not currently offered by Clojure.  This would be easier
;;; to write in Java, but I refuse to do that on principle.

(def rawops-bytecode
  (let
      [public Opcodes/ACC_PUBLIC
       static Opcodes/ACC_STATIC
       lload Opcodes/LLOAD
       iload Opcodes/ILOAD
       cw (ClassWriter. 0)]
    (.visit cw Opcodes/V1_5 (+ public Opcodes/ACC_SUPER)
            "distroverse/assem/RawOps" nil "java/lang/Object" nil)
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

(def classes-dir
  (first
   (filter (partial re-find #"/classes/$")
           (map #(.getFile %)
                (.getURLs (java.lang.ClassLoader/getSystemClassLoader))))))

(def rawops-filename (str classes-dir "distroverse/assem/RawOps.class"))

(make-parents (File. rawops-filename))

(let [existing-class (File. rawops-filename)]
  (when-not (and (.exists existing-class)
                 (= (seq (to-byte-array existing-class))
                    (seq rawops-bytecode)))
    (println "Compiling RawOps to" rawops-filename)
    (with-open [classfile (FileOutputStream. rawops-filename)]
      (copy rawops-bytecode classfile))))


