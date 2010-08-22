;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

(ns distroverse.envoy.passthrough
  (:require (clojure.contrib [command-line :as cmd-line]))
  (:use [distroverse envoy util])
  (:import [java.net Socket]))

(defn open-channel
  "Returns a SocketChannel to the specified remote host and port"
  ([host port]
     (Socket. ^String host port)))

(defn stdin-to-server
  "Reads from stdin and writes all bytes read to the given socket"
  ([^Socket chan]
     (let [os (.getOutputStream chan)]
       (loop []
         (prn "about to read from stdin")
         (let [c (char-array 1024)
               nread (.read *in* c)]
           (prn "from stdin: " nread)
           (when (pos? nread)
             (let [b (.getBytes (String. c 0 nread))]
               (.write os b 0 (count b))
               (recur))))))))

(defn server-to-stdout
  "Reads from the given socket and writes all bytes read to stdout"
  ([^Socket chan]
     (let [is (.getInputStream chan)]
       (loop []
         (prn "about to read from socket")
         (let [b (byte-array 1024)
               nread (.read is b)]
           (prn "from server: " nread)
           (when (pos? nread)
             (print (String. b 0 nread))
             (recur)))))))

(defn -main
  "The passthrough envoy does nothing more than pass unchanged to the
  client anything it receives from the server, and pass unchanged to
  the server anything it receives from the client.  For use at the
  repl, returns a function that will stop the reader/writer threads."
  ([& args]
     (cmd-line/with-command-line args
       "passthrough - a simple distroverse envoy"
       [rem-args]
       (let [[remote-uri] rem-args
             remote-host (get-host remote-uri)
             remote-port (get-port remote-uri)
             chan (open-channel remote-host remote-port)
             outbound-thread #(stdin-to-server chan)
             inbound-thread (Thread. #(server-to-stdout chan))]
         (.start inbound-thread)
         (.run outbound-thread)
         ;;;#(do (.stop outbound-thread)
         ;;;     (.stop inbound-thread))
         ))))

