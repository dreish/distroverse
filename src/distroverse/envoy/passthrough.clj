;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.envoy.passthrough
  (:use [distroverse envoy util]))

(defn new-server?
  "Do the two given URLs (given as strings rather than URL objects)
  refer to two different servers, or two different ports on the same
  server?"
  ([new-url cur-url]
     (not (and (= (get-host new-url)
                  (get-host cur-url))
               (= (get-port new-url)
                  (get-port cur-url))))))

(defn pass-to-server
  ([ob session]
     (let [server (session :server)]
       (if (and (= :url (ob :class))
                (new-server? (ob :data)
                             (session :server-url)))
         (switch-server session (ob :data))
         (when server
           (send-msg server ob))))))

(defn pass-to-client
  ([ob session]
     (send-msg (session :client)
               ob)))

(defn -main
  ([& args]
     (cmd-line/with-command-line args
       "passthrough - a simple distroverse envoy"
       [remote-url]
       (simple-envoy
        :remote-url remote-url
        :from-client pass-to-server
        :from-server pass-to-client))))

