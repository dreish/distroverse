;; Copyright (C) 2007-2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.


(ns distroverse.util
  (:import [java.net URL URI]))

(defn unchecked-byte
  "Coerce to byte without checking for numeric overflow"
  ([x]
     (.byteValue x)))

(defn get-host
  "Returns the host portion of the given URI"
  ([uri]
     (.getHost (URI. uri))))

(defn default-port-for-scheme
  "Returns the default port for schemes defined by Distroverse"
  ([scheme]
     ( {"dvtp" 1808}
       scheme )))

(defn get-port
  "Returns the port for the given URI, looking up the default port for
  the protocol associated with the URI if it does not specify a port"
  ([uri-str]
     (let [uri (URI. uri-str)
           p (.getPort uri)
           scheme (.getScheme uri)]
       (if (= -1 p)
         (if-let [p (default-port-for-scheme scheme)]
           p
           (.getDefaultPort (URL. uri-str)))
         p))))

(defn get-and-set!
  "Atomically sets the value of the given atom and returns its
  previous value."
  ([atom newval]
     (let [oldval @atom]
       (if (compare-and-set! atom oldval newval)
         oldval
         (recur atom newval)))))
