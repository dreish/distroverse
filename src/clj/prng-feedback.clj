
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


; Trivial convenience functions for interacting with the Java
; implementation of a self-shrinking linear feedback shift register,
; which has better statistical properties than a linear congruential
; PRNG, but with much less state and somewhat less startup cost than a
; Mersenne twister.  For my application, I need to go from new seed to
; a short stream of PRNs many times every second, and since I use
; immutable data structures, I don't want copy-modification to entail
; copying large amounts of state.

(import '(org.distroverse.core PRNGFeedback))
(use 'clojure.contrib.def)

(defvar- hex3ff0000000000000 (long 4607182418800017408)
  "The sign and exponent constants for IEEE 754 doubles between 1 and
  just under 2.  Just trust me on that.")

(defvar- hex3f800000 (int 1065353216)
  "The sign and exponent constants for IEEE 754 floats between 1 and
  just under 2.")

(defn- bits-to-double
  "Convert a 52-bit integer to a number between 0.0 and just under 1.
  Optional second argument provides the exponent bits; 1 will give
  results from 0.5 to 1; 2 will give results from 0.25 to 0.5; etc."
  ([l] (- (Double/longBitsToDouble (bit-or hex3ff0000000000000 l))
	  1.0))
  ([l e] (Double/longBitsToDouble
	  (bit-or l (bit-shift-left (- 1023 e) 52)))))

(defn- bits-to-float
  "Convert a 23-bit integer to a number between 0.0 and just under 1.
  Optional second argument provides the exponent bits; 1 will give
  results from 0.5 to 1; 2 will give results from 0.25 to 0.5; etc."
  ([i] (- (Float/intBitsToFloat (bit-or hex3f800000 i))
	  (float 1.0)))
  ([i e] (Float/intBitsToFloat
	  (bit-or i (bit-shift-left (- 127 e) 23)))))

(defn get-prng-double [reg-ref]
  "Return a new double between 0 and just under 1, and update the
  register referred to by reg-ref, having taken 52-60 bits from the
  output stream."
  (dosync
   (let [new-reg (.advance @reg-ref 52)]
     (ref-set reg-ref new-reg)
     (bits-to-double (.getCollectedBits new-reg)))))

(defn prng-double-seq [#^PRNGFeedback reg]
  "Returns a lazy sequence of pseudorandom doubles between 0 and just
  under 1 using the given generator, which should have been advanced
  by 52 bits."
  (lazy-cons (bits-to-double (.getCollectedBits reg))
	     (prng-double-seq (.advance reg 52))))

(defn feedback-double-seq [seed]
  "Returns a lazy sequence of pseudorandom doubles between 0 and just
  under 1 from the given long integer seed, using a PRNGFeedback
  generator."
  (prng-double-seq (.advance (PRNGFeedback. (long seed)) 52)))

(defn prng-perfect-double-seq [#^PRNGFeedback reg]
  "Returns a lazy sequence of pseudorandom doubles between 0 and just
  under 1 using the given generator, which should have been advanced
  to one.  Because of the increased accuracy in low numbers, this
  sequence will almost surely not include any exact zeros."
  (let [reg2 (.advance reg 52)]
    (lazy-cons (bits-to-double (.getCollectedBits reg2)
			       (.getNumCollectedBits reg))
	       (prng-perfect-double-seq (.advanceToOne reg2)))))

(defn feedback-perfect-double-seq [seed]
  "Returns a lazy sequence of pseudorandom doubles between 0 and just
  under 1 from the given long integer seed, using a PRNGFeedback
  generator.  Because of the increased accuracy in low numbers, this
  sequence will almost surely not include any exact zeros."
  (prng-perfect-double-seq (.advanceToOne (PRNGFeedback. (long seed)))))

(defn prng-long-seq [#^PRNGFeedback reg]
  "Returns a lazy sequence of pseudorandom longs using the given
  generator, which should have been advanced by 64 bits."
  (lazy-cons (.getCollectedBits reg)
	     (prng-long-seq (.advance reg 64))))

(defn feedback-long-seq [seed]
  "Returns a lazy sequence of pseudorandom longs from the given long
  integer seed, using a PRNGFeedback generator."
  (prng-long-seq (.advance (PRNGFeedback. (long seed)) 64)))

(defn prng-float-seq [#^PRNGFeedback reg]
  "Returns a lazy sequence of pseudorandom floats between 0 and just
  under 1 using the given generator, which should have been advanced
  by 23 bits."
  (lazy-cons (bits-to-float (.getCollectedBits reg))
	     (prng-float-seq (.advance reg 23))))

(defn feedback-float-seq [seed]
  "Returns a lazy sequence of pseudorandom floats between 0 and just
  under 1 from the given long integer seed, using a PRNGFeedback
  generator."
  (prng-float-seq (.advance (PRNGFeedback. (long seed)) 23)))


; -------------------- 8< -------------------- 8< --------------------

(comment
  ; For REPL

  (def myrng (ref (PRNGFeedback. (long 1))))

  (PRNGFeedback/nextRegister (long -1))
  ; 9223372036854775807
  (PRNGFeedback/nextRegister (long 1))
  ; -9223372036854775808


  (dorun (map (fn [x] (println (get-prng-double myrng)
			       (.getRegister @myrng)))
	      (range 40)))

  (dosync (ref-set myrng (PRNGFeedback. (long 16))))

  (dosync (ref-set myrng (PRNGFeedback. (long (discrete-** 2 15)))))
  
  (dosync (ref-set myrng (PRNGFeedback. (long (discrete-** 2 16)))))

  (dosync (ref-set myrng (PRNGFeedback. (long (discrete-** 2 17)))))
  
  (dosync (ref-set myrng (PRNGFeedback. (long (discrete-** 2 18)))))

  )

