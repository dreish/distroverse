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

(defn pass-to-server
  ([ob session]
     (send-msg (session :server)
               ob)))

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

