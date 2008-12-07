
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

(defvar universe-spec
  [{:term          "universe",
    :generator     make-fractalplace,
    :subscale      5e24,             ; ~ 528.5 mln light years
    :log-max-size  60.56,            ; ~ 21.14 bln light years
    :log-avg-size  60.56,
    :log-std-dev   0,
    :structure     [0.4 0.5 0.4],    ; Each subnode is 2/5 parent's
                                     ; size, offset is half parent's
                                     ; size + random factor of up to
                                     ; 0.3 * parent's size.
    }

   {:term          "supercluster",
    :generator     make-fractalplace,
    :subscale      3.086e23,         ; ~ 32.62 mln light years
    :log-max-size  56.87148,         ; just under (log 5e24)
    :log-avg-size  55.955,           ; ~ 211.36 mln light years
    :log-std-dev   1.0,
    :structure     [0.45 0.5 0.4],   ; Denser than above
    }

   {:term          "cluster",
    :generator     make-fractalplace,
    :subscale      3.086e21,         ; ~ 326,200 light years
    :log-max-size  54.08633,         ; just under (log 3.086e23)
    :log-avg-size  52.93494,         ; ~ 10.31 mln light years
    :log-std-dev   0.8,
    :structure     [0.5 0.5 0.4],    ; Denser still
    }

   {:term          "galaxy",
    :generator     make-fractalplace,
    :subscale      4.73e16,          ; ~ 5 light years
    :log-max-size  49.48105,         ; just under (log 3.086e21)
    :log-avg-size  47.17847,         ; ~ 32,620 light years
    :log-std-dev   1.3,
    :rot-axis      [1 0 1],
    :avg-rot-speed 8.732015e-16      ; radians per second
    :dim-skew      [1 0.1 1],        ; y-dim is 1/10 x- and z-dims
    :structure     [0.499 0.5 0.4],
    }

   {:term          "starsystem",
    :generator     make-starsystem,
    }
   ]
  "Parameters defining how universes are generated.")

