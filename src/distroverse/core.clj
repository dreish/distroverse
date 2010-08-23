;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.core
  (:require (distroverse.server [simpletest :as simpletest])
            (distroverse.envoy [passthrough :as passthrough])
            (distroverse [client :as client]))
  (:gen-class))

(def dv-help
  "Usage: dv command [options] [arguments].  Available commands:

   simpletest      Run a simple test server
   passthrough     Run a passthrough envoy
   client          Run the client

Subcommands have their own --help options.")

(defn -main
  ([subcommand & args]
     (case subcommand
        "simpletest"   (simpletest/-main args)
        "passthrough"  (passthrough/-main args)
        "client"       (client/-main args))))
