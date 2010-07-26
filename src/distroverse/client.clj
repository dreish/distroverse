;; Copyright (c) Zachary Tellman. All rights reserved.
;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.client
  (:use [penumbra opengl]
        [clojure.contrib def])
  (:require [penumbra.app :as app]))

(defvar *scene-graph*
  (ref {:pos [0 0 0]
        :children [(ref {:pos [0 0 0]
                         :shape :triangle-fan
                         :verts [0 1/2 0
                                 1/2 -1/2 1/2
                                 -1/2 -1/2 1/2
                                 -1/2 -1/2 -1/2
                                 1/2 -1/2 -1/2
                                 1/2 -1/2 1/2]
                         :color [1/4 1 1/4]})
                   (ref {:pos [0 1 0]
                         :shape :triangle-fan
                         :verts [0 1/2 0
                                 1/2 -1/2 1/2
                                 -1/2 -1/2 1/2
                                 -1/2 -1/2 -1/2
                                 1/2 -1/2 -1/2
                                 1/2 -1/2 1/2]
                         :color [1 1/4 1/4]})]})
  "Tree of nodes")

(defn run-client []
  "XXX - stub, still just playing around with penumbra at the moment"
  nil)

(defn draw-pyramid []
  (draw-triangle-fan
    (vertex 0 1 0)
    (dotimes [_ 5]
      (rotate 90 0 1 0)
      (normal 1 0.5 1)
      (vertex 0.5 0 0.5)))
  (draw-quads
    (normal 0 -1 0)
    (dotimes [_ 4]
      (rotate -90 0 1 0)
      (vertex 0.5 0 0.5))))

(defn subdivide [display-list]
  (push-matrix
    (scale 1/2 1/2 1/2)
    (push-matrix
      (translate 0 1 0)
      (material :front-and-back
                :ambient-and-diffuse [(rand) (rand) (rand) 1])
      (display-list))
    (dotimes [_ 4]
      (rotate 90 0 1 0)
      (push-matrix
        (translate 0.5 0 0.5)
        (material :front-and-back
                  :ambient-and-diffuse [(rand) (rand) (rand) 1])
        (display-list)))))

(defn sierpinski []
  (iterate
   #(create-display-list (subdivide %))
   (create-display-list (draw-pyramid))))

;;;;;;;;;;;;;;;;;

(defn init [state]
  (app/title! "Sierpinski Pyramid")
  (app/periodic-update! 2 identity)
  (enable :normalize)
  (enable :depth-test)
  (enable :cull-face)
  (enable :lighting)
  (enable :light0)
  (enable :fog)
  (shade-model :flat)
  (assoc state :pyramid (nth (sierpinski) 5)))

(defn reshape [[x y width height] state]
  (frustum-view 50 (/ (double width) height) 0.1 100)
  (load-identity)
  (translate 0 -0.35 -1.75)
  (light 0
    :position [1 1 1 0])
  (fog
    :fog-mode :exp
    :fog-density 0.75
    :fog-start 0
    :fog-end 10
    :fog-color [0 0 0 0])
  state)

(defn mouse-drag [[dx dy] _ button state]
  (assoc state
    :rot-x (+ (:rot-x state) dy)
    :rot-y (+ (:rot-y state) dx)))

(defn display [[delta time] state]
  (rotate (:rot-x state) 1 0 0)
  (rotate (:rot-y state) 0 1 0)
  ((nth (sierpinski) 5)))

(defn start []
  (app/start {:display display
              :mouse-drag mouse-drag
              :reshape reshape
              :init init}
             {:rot-x 0
              :rot-y 0
              :pyramid nil}))

