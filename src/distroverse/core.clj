;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.core
  (:require (clojure.contrib [command-line :as cmd-line])
            (distroverse [client :as client]))
  (:gen-class))

(defn -main
  ([& args]
     (cmd-line/with-command-line args
       "distroverse -- virtual reality client and server"
       [[serve? "Starts a DVTP server"]
        [port   "Port for the DVTP server" 1808]
        extra-args]
       (println args serve? port extra-args)
       (if serve?
         (throw (Exception. "Server not implemented yet"))
         (client/run-client)))))
