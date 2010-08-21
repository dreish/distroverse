;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.server.simpletest
  (:require (clojure.contrib [command-line :as cmd-line]))
  (:use [distroverse util protocol]
        [clojure.contrib server-socket]))

(def message-store (atom "first!!\n"))

(defn server-session
  "Brain-dead simple server session."
  ([in-stream out-stream]
     (let [buf (byte-array 8)
           n (.read in-stream buf)
           prev-message (get-and-set! message-store
                                      (String. buf))]
       (.write out-stream (.getBytes prev-message)))))

(defn main-
  "simpletest reads eight bytes from whatever connects to it, stores
  whatever it receives in a global atom, and after receiving that,
  sends back the previous eight bytes received (or \"first!!\\n\")."
  ([& args]
     (cmd-line/with-command-line args
       "simpletest - a simple distroverse server"
       []
       (create-server 1808 server-session))))

