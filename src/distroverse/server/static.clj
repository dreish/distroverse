;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.server.static
  (:require (clojure.contrib [command-line :as cmd-line]))
  (:use [distroverse util protocol server]
        [clojure.contrib server-socket]))

(def static-scene
     (let [cone-verts [ [0 1/2 0]
                        [1/2 -1/2 1/2]
                        [1/2 -1/2 -1/2]
                        [-1/2 -1/2 -1/2]
                        [-1/2 -1/2 1/2]
                        [1/2 -1/2 1/2] ]]
       [{:id 12
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
                   :verts cone-verts}]}]))

(defn server-session
  "Static content dumping server session."
  ([in-stream out-stream]
     (stream-send! out-stream
        (messages-to-bytes (map #(message :add-object %)
                                static-scene)))))

(defn -main
  "static dumps a static scene to the passthrough envoy and then
  disconnects."
  ([& args]
     (cmd-line/with-command-line args
       "static - a static scene server"
       []
       (create-server 1808 server-session))))
