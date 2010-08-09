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
        [clojure.contrib def]
        [cantor]
        [distroverse protocol])
  (:require [penumbra.app :as app]))

(defvar *scene-graph*
  (let [cone-verts [0 1/2 0
                    1/2 -1/2 1/2
                    1/2 -1/2 -1/2
                    -1/2 -1/2 -1/2
                    -1/2 -1/2 1/2
                    1/2 -1/2 1/2]]
    (ref {:pos [0 0 0]
          :children [(ref {:pos [0 0 0]
                           :shape :triangle-fan
                           :verts cone-verts
                           :color [1/4 1 1/4]})
                     (ref {:pos [0 1 0]
                           :shape :triangle-fan
                           :verts cone-verts
                           :color [1 1/4 1/4]})]}))
  "Tree of nodes")

(defn find-normal
  "Returns a non-normalized normal vector for a plane defined by three
  points, or two vectors.  Points of the triangle should be given
  clockwise when the triangle is viewed from the front surface, so the
  normal vector will be pointing at the viewer.  Vectors given should
  be in order such that a hand of a clock would sweep from the first
  to the second."
  ([a b c]
     (find-normal (sub b a)
                  (sub c a)))
  ([u v]
     (cross u v)))

(defn expand-triangle-fan
  "Takes a seq of numbers which, in groups of three, are the vertices
  of a triangle fan, and draws a triangle fan.  Points after the first
  should be given in clockwise order."
  ([verts]
     (let [vs (map #(apply vec3 %)
                   (partition 3 verts))
           center (first vs)]
       (draw-triangle-fan
        (vertex center)
        (vertex (second vs))
        (dorun
         (mapcat (fn [v1 v2]
                   (normal (find-normal center v1 v2))
                   (vertex v2))
                 (next vs)
                 (nnext vs)))))))

(defn run-client []
  "XXX - stub, still just playing around with penumbra at the moment"
  nil)

(defn draw-pyramid []
  (draw-triangle-fan
    (vertex 0 1 0)
    (dotimes [_ 5]
      (rotate 90 0 1 0)
      (normal (vec3 1 0.5 1))
      (vertex (vec3 0.5 0 0.5))))
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
  (println "How did I get here?")
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
  ;(assoc state :pyramid (nth (sierpinski) 5))
  )

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

(defn render-graph
  "Renders the scene graph under the given ref.  Best used inside a
  dosync with no write operations.  It is promised that render-graph
  will not perform any writes to refs."
  [graph]
  (let [[x y z] (@graph :pos)
        [r g b] (@graph :color)
        shape (@graph :shape)
        verts (@graph :verts)
        children (@graph :children)]
    (push-matrix
     (when-not (and x y z)
       (throw (Exception. "Node missing a :pos")))
     (translate x y z)
     (when (and r g b)
       (material :front-and-back
                 :ambient-and-diffuse [r g b 1]))
     (case shape
       :triangle-fan (expand-triangle-fan verts)
       nil)
     (dorun (map render-graph children)))))

(defn simple-render
  "Just trying to get something displaying"
  []
  (create-display-list
   (material :front-and-back
             :ambient-and-diffuse [1 1 0 1])
   (expand-triangle-fan
    [1/2 1 1/2
     1 0 1
     1 0 0
     0 0 0
     0 0 1
     1 0 1])))

(defn display [[delta time] state]
  (rotate (:rot-x state) 1 0 0)
  (rotate (:rot-y state) 0 1 0)
  ;((nth (sierpinski) 5))
  ((dosync (create-display-list (render-graph *scene-graph*))))
  ;((dosync (simple-render)))
  )

(defn start []
  (app/start {:display display
              :mouse-drag mouse-drag
              :reshape reshape
              :init init}
             {:rot-x 0
              :rot-y 0
              :pyramid nil}))

