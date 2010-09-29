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
     [])

(defn server-session
  "Static content dumping server session."
  ([in-stream out-stream]
     (stream-send! out-stream
                   (messages-to-bytes static-scene))))

(defn -main
  "static dumps a static scene to the passthrough envoy and then
  disconnects."
  ([& args]
     (cmd-line/with-command-line args
       "static - a static scene server"
       []
       (create-server 1808 server-session))))
