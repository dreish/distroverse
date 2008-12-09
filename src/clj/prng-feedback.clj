
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


; Trivial convenience functions for interactive with the Java
; implementation of a linear feedback shift register: better
; statistical properties than a linear congruential PRNG, but with
; zero setup cost.  For my application, I need to go from new seed to
; a short stream of PRNs many times every second.

(import '(org.distroverse.core PRNGFeedback))
(use 'clojure.contrib.def)

(defvar- hex3ff0000000000000 (long 4607182418800017408)
  "The sign and exponent constants for IEEE 754 doubles between 1 and
  just under 2.  Just trust me on that.")

(defn get-prng-real [reg-ref]
  "Return a new double between 0 and just under 1, and update the
  register referred to by reg-ref."
  (dosync
   (let [new-reg (.advance @reg-ref 52)]
     (ref-set reg-ref new-reg)
     (- (Double/longBitsToDouble (bit-or hex3ff0000000000000
					 (.getCollectedBits new-reg)))
	1.0))))



(def myrng (ref (PRNGFeedback. (long 1))))

(PRNGFeedback/nextRegister (long -1))
; 9223372036854775807 wrong?
(PRNGFeedback/nextRegister (long 1))
; -2147483648 wrong?


(dorun (map (fn [x] (println (get-prng-real myrng)
			     (.getRegister @myrng)))
	    (range 40)))

