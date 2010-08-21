;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.envoy
  (:use [distroverse util]))

(defn send-msg
  "Sends a message through a socket connection."
  ([conn msg]

     ))

(defn listener
  "Reads from"
  ([f session]
     ))

(defn simple-envoy
  "Runs a simple envoy."
  ([& arglist]
     (let [args (apply hash-map arglist)]
       (let [session (ref {})
             remote-url (args :remote-url)
             server-host (get-host remote-url)
             server-port (get-port remote-url)
             server-conn (open-connection server-host server-port)
             client-listener #(listener (args :from-client)
                                        session)
             server-listener (Thread. #(listener (args :from-server)
                                                 session))]
         (dosync (alter session assoc
                        :client-listener client-listener
                        :server-listener server-listener))
         (.start server-listener)
         (client-listener)))))

