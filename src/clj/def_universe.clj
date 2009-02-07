
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


; Generate a universe node tree from a random seed

(ns def-universe
  (:use server-lib
        prng-feedback))

(defn subseed [parent subnode-index]
  "Return a new seed for the node described by parent and
  subnode-index."
  (let [pshift   (parent :seed-mutation-shift)
	sshift   (-> pshift (+ 4) (rem 59))
	pseed    (parent :seed)
	mutation (bit-shift-left (+ 1 subnode-index) sshift)
	seedseed (bit-xor pseed mutation)]
    (long (first (prng-long-seq seedseed)))))

(defn lognormal-rand
  "Takes a normal sequence and a couple of base-e scale
  parameters (and an optional ln max) and returns a lognormal
  sequence."
  ([randseq log-avg log-std-dev]
     (map #(-> % (* log-std-dev) (+ log-avg) Math/exp float)
	  randseq))
  ([randseq log-avg log-std-dev log-max]
     (if log-max
       (let [max (float (Math/exp log-max))]
	 (map #(if (> % max) max %)
	      (lognormal-rand randseq log-avg log-std-dev)))
       (lognormal-rand randseq log-avg log-std-dev))))

(defn pick-radius [spec seed]
  (apply lognormal-rand (feedback-normal-seq seed)
	 (map spec '(:log-avg-size :log-std-dev :log-max-size))))

(defn random-quat [seed]
  "Returns a uniformly-distributed random orientation, as a
  Quaternion."
  (let [[x y z] (take 3 (feedback-normal-seq seed))
	rot (first (feedback-float-seq (inc seed)))
	theta (* rot 2 Math/PI)
	vec (if (= 0 x y z)
	      (Vector3f. 1 1 1)
	      (Vector3f. x y z))]
    (doto (Quaternion.)
      (.fromAngleAxis theta vec))))

(defn new-gen-node [parent spec subnode-index seed r move]
  {:name (spec :name)
   :generator (spec :generator)
   :radius r
   :seed seed
   :move move
   :ephemeral true
   :seed-mutation-shift (-> (parent :seed-mutation-shift)
			    inc (rem 60))
   })

(defn new-top-gen-node [parent spec pos subnode-index]
  "Returns a new highest-level node for a given layer spec."
  (let [seed (subseed parent subnode-index)
	r    (pick-radius spec (inc seed))]
    (new-gen-node parent spec subnode-index seed r
		  (pos-quat-to-moveseq pos (random-quat (+ seed 2))))))

(defn new-sub-gen-node [parent spec pos subnode-index r]
  "Returns a new highest-level node for a given layer spec."
  (let [seed (subseed parent subnode-index)]
    (new-gen-node parent spec subnode-index seed r
		  (pos-to-moveseq pos))))

(defn pseudorandom-pos [coord-scalars skews
			[unused-size offset-factor rand-factor]
			radius prngs]
  "Generate a reproduceable pseudorandom location for a new subnode.
  Returns coordinates as a sequence: (x y z)."
  (let [offset (* offset-factor radius)
	rand-scale (* rand-factor radius)]
    (map (fn [rng scalar skew]
	   (-> offset (* scalar skew)
	              (+ (* rng rand-scale))
		      (- (/ rand-scale 2))))
	 prngs
	 coord-scalars
	 skews)))

(defn gen-fractalplace [parent spec]
  "Returns a sequence of 8 subnodes for the given parent node."
  (let [parent-layer (parent :layer)
	parent-rad   (parent :radius)
	layer-spec   (spec parent-layer)
	structure    (layer-spec :structure)
	size-factor  (structure 0)
	my-radius    (* size-factor parent-rad)
	next-layer?  (< my-radius (layer-spec :subscale))
	subnode-gen
	  (if next-layer?
	    #(new-top-gen-node parent (spec (dec parent-layer)) %1 %2)
	    #(new-sub-gen-node parent layer-spec %1 %2 my-radius))]
    (map subnode-gen
	 (map pseudorandom-pos
	      (for [xo [-1 1] yo [-1 1] zo [-1 1]]
		(list xo yo zo))
	      (repeat (or (parent :dim-skew) [1 1 1]))
	      (repeat strucure)
	      (repeat parent-rad)
	      (partition 3 (feedback-float-seq (parent :seed))))
	 (range 1 9))))

(defn- check-structure [s]
  "Throws an exception if the structure constants would violate the
  rule that all subnodes of a parent node must fit within the parent
  node."
  (let [[size offset randfactor] s
	totaloffset (+ offset (/ randfactor 2))
	maxdistance (+ (Math/sqrt (* totaloffset totaloffset 3))
		       size)]
    (if (> maxdistance 1.0)
      (throw (Exception. (str "Structure too big by a factor of "
			      maxdistance ", try "
			      (with-out-str
			       (prn (map #(/ % maxdistance) s)))))))))

(defn new-universe-spec [& layer-specs]
  "Returns a vector containing the given layer specs, each one a hash,
  adding a :subspec key in each spec that maps to the one after it (or
  nil in the last layer spec)."
  (map #(do
	  (check-structure ((first %) :structure))
	  (assoc (first %)
	    :subspec (second %)
	    :subscale (if (second %)
			(* (Math/exp (:log-max-size (second %)))
			   ((:structure (first %)) 0)))))
       (rests layer-specs)))

(defvar universe-spec
  (new-universe-spec
   {:name          "universe",
    :generator     gen-fractalplace,
    :log-max-size  60.56,            ; ~ 21.14 bln light years
    :log-avg-size  60.56,
    :log-std-dev   0,
    :structure     [0.4 0.2 0.25],   ; Each subnode is 2/5 parent's
                                     ; size, offset is 1/5 parent's
                                     ; radius + random factor of up to
                                     ; 1/4 * parent's radius.
    }

   {:name          "supercluster",
    :generator     gen-fractalplace,
    :log-max-size  56.87148,         ; ~ 528.5 mln light years
    :log-avg-size  55.955,           ; ~ 211.36 mln light years
    :log-std-dev   1.0,
    :structure     [0.44 0.19 0.24], ; Denser than above
    }

   {:name          "cluster",
    :generator     gen-fractalplace,
    :log-max-size  54.08633,         ; ~ 32.62 mln light years
    :log-avg-size  52.93494,         ; ~ 10.31 mln light years
    :log-std-dev   0.8,
    :structure     [0.47 0.188 0.235], ; Denser still
    }

   {:name          "galaxy",
    :generator     gen-fractalplace,
    :log-max-size  49.48105,         ; ~ 326,200 light years
    :log-avg-size  47.17847,         ; ~ 32,620 light years
    :log-std-dev   1.3,
    :rot-axis      [1 0 1],
    :avg-rot-speed 8.732015e-16      ; radians per second
    :dim-skew      [1 0.1 1],        ; y-dim is 1/10 x- and z-dims
    :structure     [0.4344 0.2177 0.2177],
    }

   {:name          "starsystem",
    :generator     gen-starsystem,
    :log-max-size  38.39528,         ; ~ 5 light years
    :log-avg-size  38.39528,
    :log-std-dev   0,
    }
   )
  "Parameters defining how universes are generated.")

