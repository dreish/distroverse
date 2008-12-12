
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


(defn new-topnode [spec [x y z seed]]
  ;XXX
  )

(defn new-subnode [spec [x y z seed] r]
  ;XXX
  )

(defn prng-float-seq [seed]
  ""
  ;XXX
  )

(defn pseudorandom-pos [coord-scalars skew structure radius prng-seq]
  "Returns a list: (x y z)."
  (let [new-seed (bit-xor seedchange (bit-shift-left parent-seed 3))]
    (map (fn [& args] (apply map * args))
	 (groups-of 3 prng-seq)
	 (repeat skew)
	 coord-scalars)))

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
	    #(new-topnode parent (spec (dec parent-layer)) %1 %2)
	    #(new-subnode parent layer-spec %1 %2 my-radius))]
    (map subnode-gen
	 (map pseudorandom-pos
	      (for [xo [-1 1] yo [-1 1] zo [-1 1]]
		(list xo yo zo))
	      (repeat (or (parent :dim-skew) [1 1 1]))
	      (repeat strucure)
	      (repeat parent-rad)
	      (feedback-float-seq (parent :seed)))
	 (range 1 9))))


(defvar universe-spec
  [{:term          "universe",
    :generator     gen-fractalplace,
    :subscale      5e24,             ; ~ 528.5 mln light years
    :log-max-size  60.56,            ; ~ 21.14 bln light years
    :log-avg-size  60.56,
    :log-std-dev   0,
    :structure     [0.4 0.2 0.25],   ; Each subnode is 2/5 parent's
                                     ; size, offset is 1/5 parent's
                                     ; radius + random factor of up to
                                     ; 1/4 * parent's radius.  The sum
                                     ; of these three numbers must
                                     ; always be at most 1.
    }

   {:term          "supercluster",
    :generator     gen-fractalplace,
    :subscale      3.086e23,         ; ~ 32.62 mln light years
    :log-max-size  56.87148,         ; just under (log 5e24)
    :log-avg-size  55.955,           ; ~ 211.36 mln light years
    :log-std-dev   1.0,
    :structure     [0.45 0.2 0.25],  ; Denser than above
    }

   {:term          "cluster",
    :generator     gen-fractalplace,
    :subscale      3.086e21,         ; ~ 326,200 light years
    :log-max-size  54.08633,         ; just under (log 3.086e23)
    :log-avg-size  52.93494,         ; ~ 10.31 mln light years
    :log-std-dev   0.8,
    :structure     [0.5 0.2 0.25],   ; Denser still
    }

   {:term          "galaxy",
    :generator     gen-fractalplace,
    :subscale      4.73e16,          ; ~ 5 light years
    :log-max-size  49.48105,         ; just under (log 3.086e21)
    :log-avg-size  47.17847,         ; ~ 32,620 light years
    :log-std-dev   1.3,
    :rot-axis      [1 0 1],
    :avg-rot-speed 8.732015e-16      ; radians per second
    :dim-skew      [1 0.1 1],        ; y-dim is 1/10 x- and z-dims
    :structure     [0.499 0.25 0.25],
    }

   {:term          "starsystem",
    :generator     gen-starsystem,
    :log-avg-size  38.39528,         ; just under (log 4.73e16)
    :log-std-dev   0,
    }
   ]
  "Parameters defining how universes are generated.")

