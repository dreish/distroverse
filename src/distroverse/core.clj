(ns distroverse.core
  (:require (clojure.contrib [command-line :as cmd-line]))
  (:gen-class))

(defn -main [& args]
  (cmd-line/with-command-line args
    "distroverse -- virtual reality client and server"
    [[serve? "Starts a DVTP server"]
     [port   "Port for the DVTP server" 1808]
     extra-args]
    (println args serve? port extra-args)))

