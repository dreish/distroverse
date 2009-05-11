
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


(ns dvtp-lib
  (:use util
        clojure.contrib.def))

(import '(java.util.concurrent ArrayBlockingQueue TimeUnit))

(import '(com.jme.math Quaternion Vector3f))
(import '(org.distroverse.dvtp Quat Vec Move MoveSeq AskInv ReplyInv
                               GetCookie Cookie FunCall FunRet))
(import '(org.distroverse.core.net NetSession DvtpChannel))


(defvar response-map
  {AskInv       #(ReplyInv. (.getKey %)),
   GetCookie    #(Cookie. (.getKey %)),
   FunCall      #(FunRet. (.getContents % 0))}
  "Defines the classes that are expected in response to each message
  class, and a vector of methods to call on the message object to
  build up a list of arguments to pass to a constructor for the
  response class to make a matcher object.")

(defn lookup-response-func [message]
  "Lookup a response matcher to the given message.  Throws an exception
  if the given message does not call for a response."
  (let [response-code (response-map (class message))]
    (if response-code
      (response-code message)
      (throw (Exception. (str "No response to " (class message)))))))

(defvar message-set
  #{'AddObject. 'AskInv.        'Blob.  'ClearShape.    'Click.
    'Click2.    'Cookie.        'DLong. 'DNode. 'DNodeRef.
    'Dict.      'Frac.  'GetCookie.     'Real.  'ReparentObject.
    'ReplyInv.  'SetShape.      'SetVisible.    'ULong. 'ConPerm.
    'DList.     'DeleteObject.  'DisplayUrl.    'DvtpExternalizable.
    'DvtpObject.        'Err.   'False. 'Flo.   'FunCall.
    'FunRet.    'KeyDown.       'KeyUp. 'Keystroke.     'MoreDetail.
    'Move.      'MoveObject.    'MoveSeq.       'Pair.  'PointArray.
    'ProxySpec. 'Quat.  'RedirectUrl.   'SetUrl.        'Shape.
    'Str.       'True.  'Vec.   'Warp.  'WarpObject.    'WarpSeq.}
  "Lists the constructors of DvtpExternalizable classes that either
  are messages sent by this server, or are components of those
  messages.  (Additional classes not included in this description may
  also be listed.)")

(defmacro import-dvtp
  "Import all DVTP classes that might be used by the server."
  []
  (let [classes (map #(+> % name seq drop-last (apply str _) symbol)
                     message-set)]
    `(import '(org.distroverse.dvtp ~@classes))))

(defn const-message? [mc]
  "Is the message object described by the given code completely
  determined at compile-time?  (Err on the side of returning false, if
  at all.)"
  (cond (not (list? mc))
          false
        (and (symbol? (first mc))
             (message-set (first mc))
             (every? #(or (number? %)
                          (string? %)
                          (const-message? %))
                     (next mc)))
          true
        :else
          false))

(defmacro lookup-response [message]
  "Look up the response matcher to the given message, at compile time
  if possible, or else at runtime."
  (if (const-message? message)
    (lookup-response-func (eval message))
    `(lookup-response-func ~message)))

(defn send-message
  [msg #^DvtpChannel chan]
  (.send chan msg))

(defn add-callback
  "XXX TODO"
  []
  ; XXX
  (throw (Exception. "implement add-callback")))

(defn functional-sync-call
  "Send the given inquiry message along the given channel,
  synchronously wait for the result for up to timeout milliseconds,
  and return it (or nil on failure).  This technically involves side
  effects, but should in practice be safe to call in a transaction as
  long as either the timeout is reasonably small, or the transaction
  involves no writes."
  [message channel timeout]
  (let [reply-queue (ArrayBlockingQueue. 1 false)]
    ;; Need a generic add-callback -- how?
    (add-callback channel
                  (lookup-response-func message)
                  #(.offer reply-queue %))
    (send-message message channel)
    (.poll reply-queue timeout TimeUnit/MILLISECONDS)))

