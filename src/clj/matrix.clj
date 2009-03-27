
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


;; Operations with matrices.  They'll be immutable values as long as
;; you never call methods on them outside of matrix.clj.

(ns matrix)

(import '(com.jme.math Quaternion Vector3f Matrix4f))

(def +Midentity+ (Matrix4f.))

(defmulti invert "Inverts a matrix" class)

(defmethod invert Matrix4f [m] (.invert m))

(defmulti M* "Multiply two matrices, or a matrix and a vector"
  (fn [a b & more] [(class a) (class b)]))

(defmethod M* [Matrix4f Matrix4f]
  ([a b]
     (.mult a b))
  ([a b c & more]
     (reduce M* (concat (list a b c) more))))

(defmethod M* [Matrix4f Vector3f]
  ([a b]
     (.mult a b))
  ([a b c & more]
     (reduce M* (concat (list a b c) more))))

(defmulti to-matrix
  "Convert a translation vector or rotation quaternion into a
  transformation matrix."
  class)

(defmethod to-matrix Vector3f
  [v]
  (doto (Matrix4f.)
    (.setTranslation v)))

(defmethod to-matrix Quaternion
  [q]
  (doto (Matrix4f.)
    (.setRotationQuaternion q)))

(defmethod to-matrix Matrix4f [m] m)
