
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


; Generate a universe node tree from a random seed

(ns def-universe
  (:require [durable-maps :as dm])
  (:use [server-lib])
  (:use [prng-feedback])
  (:use [clojure.contrib.def]))

(import '(com.jme.math Quaternion Vector3f))


(defn subseed
  "Return a new seed for the node described by parent and
  subnode-index."
  [parent subnode-index]
  (let [pshift   (parent :seed-mutation-shift)
        sshift   (-> pshift (+ 4) (rem 59))
        pseed    (parent :seed)
        mutation (bit-shift-left (+ 1 subnode-index) sshift)
        seedseed (bit-xor pseed mutation)]
    (long (first (feedback-long-seq seedseed)))))

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
  (first
   (apply lognormal-rand (feedback-normal-seq seed)
          (map spec '(:log-avg-size :log-std-dev :log-max-size)))))

(defn random-quat
  "Returns a uniformly-distributed random orientation, as a
  Quaternion."
  [seed]
  (let [[x y z] (take 3 (feedback-normal-seq seed))
        rot (first (feedback-float-seq (inc seed)))
        theta (* rot 2 Math/PI)
        vec (if (= 0 x y z)
              (Vector3f. 1 1 1)
              (Vector3f. x y z))]
    (doto (Quaternion.)
      (.fromAngleAxis theta vec))))

(defn- new-gen-node [parent depth spec lspec subnode-index seed r moveseq]
  "Return a new ephemeral node for the given parameters."
  {:name (lspec :name)
   :generator (lspec :generator)
   :layer (lspec :layer)
   :spec spec
   :radius r
   :seed seed
   :moveseq moveseq
   :ephemeral true
   :seed-mutation-shift (-> (parent :seed-mutation-shift)
                            (+ 4)
                            (rem 59))
   :depth depth
   :parent-ref parent
   :parent (parent :nodeid)             ; might be nil
   ; XXX need the path from a concrete node here
   })

(defn new-top-gen-node
  "Returns a new highest-level node for a given layer spec."
  [parent depth spec lspec pos subnode-index]
  (let [seed (subseed parent subnode-index)
        r    (pick-radius lspec (inc seed))]
    (new-gen-node parent depth spec lspec subnode-index seed r
                  (pos-quat-to-moveseq pos (random-quat (+ seed 2))))))

(defn new-sub-gen-node
  "Returns a new highest-level node for a given layer spec."
  [parent depth spec lspec pos subnode-index r]
  (let [seed (subseed parent subnode-index)]
    (new-gen-node parent depth spec lspec subnode-index seed r
                  (pos-to-moveseq pos))))

(defn pseudorandom-pos
  "Generate a reproduceable pseudorandom location for a new subnode.
  Returns coordinates as a sequence: (x y z)."
  [coord-scalars [unused-size offset-factor rand-factor]
   skews radius prngs]
  (let [offset (* offset-factor radius)
        rand-scale (* rand-factor radius)]
    (map (fn [rng scalar skew]
           (-> offset (* scalar skew)
                      (+ (* rng rand-scale))
                      (- (/ rand-scale 2))))
         prngs
         coord-scalars
         skews)))

(defvar- ntvars (dm/dmsync (dm/get-map (str ws-ns "node-tree/vars"))))

(defn gen-echildren
  [spec-dm-varname par-radius par-layer par-depth par-seed]
  (let [spec (dm/dmsync (:val (ntvars spec-dm-varname)))
        fakeparent {:radius par-radius
                    :layer par-layer
                    :depth par-depth
                    :seed par-seed}
        generator (:generator (spec par-layer))]
    (generator fakeparent spec par-layer)))

(defn gen-fractalplace
  "Returns a sequence of 8 subnodes for the given parent node."
  ([parent spec]
     (let [parent-layer (parent :layer)]
       (gen-fractalplace parent spec parent-layer)))
  ([parent spec parent-layer]
     (let [parent-rad   (parent :radius)
           layer-spec   (spec parent-layer)
           structure    (layer-spec :structure)
           size-factor  (structure 0)
           my-radius    (* size-factor parent-rad)
           next-layer?  (< my-radius (layer-spec :subscale))
           depth        (inc (parent :depth))
           subnode-gen
           (if next-layer?
             #(new-top-gen-node parent depth spec
                                (spec (inc parent-layer))
                                %1 %2)
             #(new-sub-gen-node parent depth spec
                                layer-spec
                                %1 %2 my-radius))]
       (map subnode-gen
            (map pseudorandom-pos
                 (for [xo [-1 1] yo [-1 1] zo [-1 1]]
                   (list xo yo zo))
                 (repeat (or (parent :dim-skew) [1 1 1]))
                 (repeat structure)
                 (repeat parent-rad)
                 (partition 3 (feedback-float-seq (parent :seed))))
            (range 1 9)))))

(defn gen-children
  "Return a lazy seq of the children of the given ephemeral node."
  [n & args]
  (let [gen (n :generator)
        spec (n :spec)]
    (apply gen n spec args)))

(defn gen-starsystem
  ""
  ;; XXX
  )

(defn gen-simple-starsystem
  "Returns a sequence of subnodes making up the given star system."
  ([parent spec]
     (list
      {:radius ;XXX
       :moveseq ;XXX
       :ephemeral true
       :shape (polyhedron :rows 4)
       :depth ;XXX
       :parent-ref parent
       :parent (parent :nodeid)
       })))

(defn- check-structure
  "Throws an exception if the structure constants would violate the
  rule that all subnodes of a parent node must fit within the parent
  node."
  [s name]
  (let [[size offset randfactor] s
        totaloffset (+ offset (/ randfactor 2))
        maxdistance (+ (Math/sqrt (* totaloffset totaloffset 3))
                       size)]
    (if (> maxdistance 1.0)
      (throw (Exception. (str name " structure too big by a factor of "
                              maxdistance ", try "
                              (with-out-str
                               (prn (map #(/ % maxdistance) s)))))))))

(defn new-universe-spec
  "Returns a seq containing the given layer specs, each one a hash,
  adding a :subscale key in each spec that gives the size threshhold
  at which the next layer should be used (or 0 in the last layer
  spec)."
  [& layer-specs]
  (do
    (dorun (map #(if (= gen-fractalplace (% :generator))
                   (check-structure (% :structure) (% :name)))
                layer-specs))
    (into []
          (map #(assoc (first %1)
                  :subscale (if (second %1)
                              (* (Math/exp (:log-max-size (second %)))
                                 ((:structure (first %1)) 0))
                              0)
                  :layer %2)
               (rests layer-specs)
               (iterate inc 0)))))

(defn new-universe
  "Generate a new top node for the given universe-spec and random
  seed.  N.B.: returns an ephemeral node."
  [spec seed]
  (new-gen-node {:seed-mutation-shift 0}
                (first spec)
                0
                seed
                (pick-radius (spec 0) (inc seed))
                (pos-to-moveseq [0 0 0])))

(defvar big-universe-spec
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
    :structure     [0.43439 0.21769 0.21769],
    }

   {:name          "starsystem",
    :generator     gen-starsystem,
    :log-max-size  38.39528,         ; ~ 5 light years
    :log-avg-size  38.39528,
    :log-std-dev   0,
    }
   )
  "Parameters defining how universes are generated.")

(defvar small-universe-spec
  (new-universe-spec
   {:name          "stellarcluster",
    :generator     gen-fractalplace,
    :log-max-size  40.0,             ; ~24.88 light years
    :log-avg-size  40.0,
    :log-std-dev   0,
    :structure     [0.4349 0.21769 0.21769],
    }
   
   {:name          "starsystem",
    :generator     gen-simple-starsystem,
    :log-max-size  38.39528,         ; ~ 5 light years
    :log-avg-size  38.39528,
    :log-std-dev   0,
    }
   )
  "Parameters for a small, simple universe of just a few polyhedral
  stars in an open cluster.")
