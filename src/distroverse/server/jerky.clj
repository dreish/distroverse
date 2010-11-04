;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.server.jerky
  (:require (clojure.contrib [command-line :as cmd-line]))
  (:use [distroverse util protocol server]
        [clojure.contrib server-socket])
  (:import [java.util.concurrent LinkedBlockingQueue]
           [java.io IOException]))

(def static-scene
     (let [cone-verts [ [0 1/2 0]
                        [1/2 -1/2 1/2]
                        [1/2 -1/2 -1/2]
                        [-1/2 -1/2 -1/2]
                        [-1/2 -1/2 1/2]
                        [1/2 -1/2 1/2] ]]
       (ref [{:id 12
              :pid 1
              :begin 0
              :moves (pos-to-moveseq 0 0 0)
              :shapes []}
             {:id 13
              :pid 12
              :begin 0
              :moves (pos-to-moveseq 1 0 0)
              :shapes [{:tripat :triangle-fan
                        :color [1/4 1 3/4]
                        :verts cone-verts}]}
             {:id 14
              :pid 12
              :begin 0
              :moves [{:poly [{:move [1 1 0],
                               :rps 1.0
                               :rot {:w 1.0,
                                     :x 0.0,
                                     :y 0.0,
                                     :z 0.0}}
                              {:move [0 0 0],
                               :rps 1.0
                               :rot {:w 0.0
                                     :x 0.0
                                     :y 1.0
                                     :z 0.0}}],
                       :timebase 0.0,
                       :sines [],
                       :dur Infinity}]
              :shapes [{:tripat :triangle-fan
                        :color [1 1/4 3/4]
                        :verts cone-verts}]}])))

(def event-queues (ref #{}))

(defn server-session
  "Jerky content dumping server session."
  ([in-stream out-stream]
     (let [queue (LinkedBlockingQueue.)
           scene (dosync
                  (alter event-queues conj queue)
                  @static-scene)]
       (stream-send! out-stream
           (messages-to-bytes (map #(message :add-object %)
                                   scene)))
       (.flush out-stream)
       (loop []
         (let [h (.take queue)]
           (if (try
                 (stream-send! out-stream
                    (message-to-bytes h))
                 (.flush out-stream)
                 true
                 (catch IOException e
                   nil))
             (recur))))
       (dosync (alter event-queues disj queue)))))

;;; XXX static-scene really needs to be a map or something.  This is
;;; ridiculous.
(defn apply-mod-func
  "Takes a vector, a nodeid, and a modification function and returns a
  vector the with modification function used to modify just the node
  with the given nodeid."
  ([v nodeid f]
     (vec (map #(if (= nodeid (:id %))
                  (f %)
                  %)
               v))))

(defn modify-scene
  "Modifies a node in the static-scene and sends a message to all
  connected clients informing them of the change"
  ([nodeid mod-func message]
     (dosync
      (ensure event-queues)
      (alter static-scene apply-mod-func nodeid mod-func)
      (let [eqs @event-queues]
        (trans-io
         (doseq [eq eqs]
           (.add eq message)))))))

(defn random-rotation
  "Returns a random move seq, stationary at the given X, Y, and Z
  coordinates."
  ([x y z]
     [{:poly [{:move [x y z],
               :rps 1.0
               :rot {:w 1.0,
                     :x 0.0,
                     :y 0.0,
                     :z 0.0}}
              {:move [0 0 0],
               :rps 1.0
               :rot (normalize-quat {:w (rand) :x (rand)
                                     :y (rand) :z (rand)})}]
       :timebase 0.0,
       :sines [],
       :dur Infinity}]))

(defn move-to
  "Given a moveseq, returns a function that can be applied to an add-object
  message, changing its moveseq to the one given."
  ([ms]
     (fn [aom]
       (assoc aom :moves ms))))

(defn jerk-state
  "Occasionally changes the rotation of node 14."
  ([]
     (Thread/sleep 10000)
     (let [newrot (random-rotation 1 1 0)]
       (modify-scene 14
                     (move-to newrot)
                     (message :move-object
                              {:id 14
                               :begin 0.0
                               :moves newrot})))
     (recur)))

(defn -main
  "jerky dumps a static scene to the passthrough envoy and then makes
  changes to it every several seconds."
  ([& args]
     (cmd-line/with-command-line args
       "jerky - a jerky scene server"
       []
       (.start (Thread. jerk-state))
       (create-server 1808 server-session))))
