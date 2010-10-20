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
        [distroverse protocol util])
  (:require [penumbra.app :as app]))

(defvar scene-graph
  (let [cone-verts [ [0 1/2 0]
                     [1/2 -1/2 1/2]
                     [1/2 -1/2 -1/2]
                     [-1/2 -1/2 -1/2]
                     [-1/2 -1/2 1/2]
                     [1/2 -1/2 1/2] ]]
    (ref {:id 1
          :pid nil
          :pos [0 0 0]
          :shapes []
          :children [(ref {:id 2
                           :pid 1
                           :pos [0 0 0]
                           :children []
                           :shapes [{:tripat :triangle-fan
                                     :color [1/4 1 1/4]
                                     :verts cone-verts}]})
                     (ref {:id 3
                           :pid 1
                           :pos [0 1 0]
                           :children []
                           :shapes [{:tripat :triangle-fan
                                     :color [1 1/4 1/4]
                                     :verts cone-verts}]})]}))
  "Tree of nodes")

(defvar id-to-object
  (ref {1 scene-graph
        2 (-> scene-graph :children first)
        3 (-> scene-graph :children second)}))

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
  "Takes a seq of groups of three numbers numbers which are the
  vertices of a triangle fan, and draws a triangle fan.  Points after
  the first should be given in clockwise order."
  ([verts]
     (let [vs (map (partial apply vec3) verts)
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
  (app/title! "Distroverse Viewer")
  (app/periodic-update! 30 identity)
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
        shapes (@graph :shapes)
        children (@graph :children)]
    (push-matrix
     (when-not (and x y z)
       (throw (Exception. "Node missing a :pos")))
     (translate x y z)
     (doseq [shape shapes]
       (let [tripat  (shape :tripat)
             verts   (shape :verts)
             [r g b] (shape :color)]
         (when (and r g b)
           (material :front-and-back
                     :ambient-and-diffuse [r g b 1]))
         (case tripat
            :triangle-fan (expand-triangle-fan verts)
            nil)))
     (dorun (map render-graph children)))))

(defn simple-render
  "Just trying to get something displaying"
  []
  (create-display-list
   (material :front-and-back
             :ambient-and-diffuse [1 1 0 1])
   (expand-triangle-fan
    [ [1/2 1 1/2]
      [1 0 1]
      [1 0 0]
      [0 0 0]
      [0 0 1]
      [1 0 1] ])))

(defn display [[delta time] state]
  (rotate (:rot-x state) 1 0 0)
  (rotate (:rot-y state) 0 1 0)
  ;;;((nth (sierpinski) 5))
  (dosync ((create-display-list (render-graph scene-graph))))
  ;;;((dosync (simple-render)))
  )

(defn start []
  (app/start {:display display
              :mouse-drag mouse-drag
              :reshape reshape
              :init init}
             {:rot-x 0
              :rot-y 0
              :pyramid nil}))

(defn run-client []
  "XXX - stub, still just playing around with penumbra at the moment"
  nil)

(def envoy-listener-thread (atom nil))

(def envoy-process (atom nil))

;;; OutputStream to the envoy
(def output-to-envoy (atom nil))

;;; Agent for serializing function calls writing to output-to-envoy
(def messages-to-envoy (agent nil))

(defmulti handle-message
  "Act on the given message from the envoy"
  message-type)

(defmethod handle-message :add-object
  ([m]
     (println "I'm the client!  And I got an add-object message!  Here:"
              m)
     (let [mv (message-value m)
           id (mv :id)
           pid (mv :pid)
           newnode (ref (assoc mv :children []))]
       (dosync
        (let [parent (id-to-object pid)]
          (when parent
            (alter parent assoc :children
                   (conj (parent :children)
                         newnode)))
          (alter id-to-object assoc id newnode))))))

(defn handle-messages
  "Reads messages from the given InputStream until the stream closes,
  and acts on those messages"
  ([is]
     (doseq [m (bytes-to-messages (is-to-byteseq is))]
       (handle-message m))))

;;; uri would, in the finished product, come to the client by way of a
;;; location: bar.
(let [uri "dvtp://localhost/"]
  (defn run-envoy!
    "Runs the passthrough envoy (hardcoded for the moment), starts a
  thread to listen for messages from the envoy, and initializes the
  agent for sending messages to the envoy"
    ([]
       (let [proc (forkexec (str "./dv passthrough " uri))
             is (.getInputStream proc)
             os (.getOutputStream proc)]
         (reset! envoy-process proc)
         (reset! envoy-listener-thread
                 (Thread. #(handle-messages is)))
         (.start @envoy-listener-thread)
         (reset! output-to-envoy os)))))

(defn -main
  ([args]
     (run-envoy!)
     (start)))

