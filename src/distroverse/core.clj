;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.core
  (:require (distroverse.server [simpletest :as simpletest]
                                [static :as static]
                                [jerky :as jerky])
            (distroverse.envoy [passthrough :as passthrough])
            (distroverse [client :as client]))
  (:gen-class))

(def dv-help
  "Usage: dv command [options] [arguments].  Available commands:

   server          Run one of the servers (server --help for list)
   passthrough     Run a passthrough envoy
   client          Run the client
   help or --help  Print this message

Subcommands have their own --help options.")

(def dv-server-help
  "Usage: dv server servername [arguments].  Available servernames:

   simpletest  A non-DVTP test of the basic server code, taking from
               each connection an eight-byte message and responding
               with the eight bytes left by the last connection
   static      A DVTP server that dumps a static scene to the
               passthrough envoy
   --help      Print this message")

(defn print-help
  ([]
     (println dv-help)))

(defn print-server-help
  ([]
     (println dv-server-help)))

(defn run-server
  ( [ [subcommand & args] ]
    (case subcommand
       "simpletest"  (simpletest/-main args)
       "static"      (static/-main args)
       "jerky"       (jerky/-main args)
       "--help"      (print-server-help)) ))

(defn -main
  ([subcommand & args]
     (case subcommand
        "server"       (run-server args)
        "passthrough"  (passthrough/-main args)
        "client"       (client/-main args)
        "help"         (print-help)
        "--help"       (print-help))))

(defn -main-cmdlineargs
  ([]
     (let [clarg *command-line-args*]
       (if (nil? clarg)
         (print-help)
         (apply -main *command-line-args*)))))

