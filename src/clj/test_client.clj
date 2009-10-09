
;; <copyleft>

;; Copyright 2008-2009 Dan Reish

;; This program is free software; you can redistribute it and/or
;; modify it under the terms of the GNU General Public License as
;; published by the Free Software Foundation; either version 3 of the
;; License, or (at your option) any later version.

;; This program is distributed in the hope that it will be useful, but
;; WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
;; General Public License for more details.

;; You should have received a copy of the GNU General Public License
;; along with this program; if not, see <http://www.gnu.org/licenses>.

;; Additional permission under GNU GPL version 3 section 7

;; If you modify this Program, or any covered work, by linking or
;; combining it with clojure-contrib (or a modified version of that
;; library), containing parts covered by the terms of the Eclipse
;; Public License, the licensors of this Program grant you additional
;; permission to convey the resulting work. {Corresponding Source for
;; a non-source form of such a combination shall include the source
;; code for the parts of clojure-contrib used as well as that of the
;; covered work.}

;; </copyleft>

(ns test-client
  (:use dvtp-lib)
  (:import [java.util.concurrent LinkedBlockingQueue TimeUnit]
           [java.net InetSocketAddress]
           [org.distroverse.core.net DvtpMultiplexedClient
                DvtpFlexiParser DvtpFlexiStreamer
                InvertingInQueueObjectWatcher]))

(import-dvtp)

;;; All times in milliseconds
(def *pause* 100)
(def *timeout* 400)
(def *session* nil)

(def *mplexer*
     (DvtpMultiplexedClient. DvtpFlexiParser
                             DvtpFlexiStreamer))

(def !events! (LinkedBlockingQueue.))

(let [watcher (InvertingInQueueObjectWatcher. !events!)]
  (.setWatcher *mplexer* watcher)
  (.start watcher)
  (.start *mplexer*))


(defn dvtp-send!
  "Send a single object."
  ([o]
     (prn "Sending:" o)
     (-> *session* .getNetOutQueue (.add o))))

(defn dvtp-recv!
  "Keep receiving and printing objects until *timeout* seconds pass
  with no object received.  Returns nil."
  ([]
     (prn "Receiving ...")
     (loop []
       (when-let [next-ob (.poll !events!
                                 *timeout* TimeUnit/MILLISECONDS)]
         (prn "Received:" [(.a next-ob)
                           (.b next-ob)])
         (recur)))))

(defn dvtp-connect!
  ([host port]
     (.connect *mplexer* (InetSocketAddress. host port))))

(defn pause
  []
  (Thread/sleep *pause*))

(defn getall!
  ([]
     (dvtp-recv!)
     (pause)))

(defn run-test!
  ([]
    (binding [*session* (dvtp-connect! "localhost" 1808)]
      (with-open [*session* *session*]
        (getall!)
        (doseq [msg ["LOCATION /"
                     "ENVOYOPEN"
                     (ReplyInv. (Str. "id")
                                (Str. "foo"))
                     (fun-ret 0 "bar")]]
          (dvtp-send! msg)
          (getall!))))))

