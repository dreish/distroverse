
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

(ns server-lib
  (:use util
        clojure.contrib.def))

(import '(com.jme.math Quaternion Vector3f))
(import '(org.distroverse.dvtp Quat Vec Move MoveSeq AskInv ReplyInv
                               GetCookie Cookie FunCall FunRet))
(import '(org.distroverse.core.net NetSession))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; General sequence functions

(defn rests [coll]
  "Returns a lazy sequence of successive rests of coll, beginning with
  the entire collection and ending with a collection of count 1."
  (take-while identity (iterate rest coll)))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Miscellaneous general functions

(defn do-or [& args]
  "(or) as a function, evaluating all its arguments, but returning the
  first true one just as (or) does."
  (reduce #(or %1 %2) args))

(defn gmt-time-string []
  (. (java.util.Date.) toGMTString))

(let [rng (java.security.SecureRandom.)]
  (defn sec-random []
    "Get a pseudorandom 32-bit int.  FIXME seed with secure random
  bytes, such as from /dev/urandom."
    (locking rng
      (.nextInt rng))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Communications functions

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
  #{'AddObject.	'AskInv.	'Blob.	'ClearShape.	'Click.
    'Click2.	'Cookie.	'DLong.	'DNode.	'DNodeRef.
    'Dict.	'Frac.	'GetCookie.	'Real.	'ReparentObject.
    'ReplyInv.	'SetShape.	'SetVisible.	'ULong.	'ConPerm.
    'DList.	'DeleteObject.	'DisplayUrl.	'DvtpExternalizable.
    'DvtpObject.	'Err.	'False.	'Flo.	'FunCall.
    'FunRet.	'KeyDown.	'KeyUp.	'Keystroke.	'MoreDetail.
    'Move.	'MoveObject.	'MoveSeq.	'Pair.	'PointArray.
    'ProxySpec.	'Quat.	'RedirectUrl.	'SetUrl.	'Shape.
    'Str.	'True.	'Vec.	'Warp.	'WarpObject.	'WarpSeq.}
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
		     (rest mc)))
	  true
	:else
	  false))

(defmacro lookup-response [message]
  "Look up the response matcher to the given message, at compile time
  if possible, or else at runtime."
  (if (const-message? message)
    (lookup-response-func (eval message))
    `(lookup-response-func ~message)))

(defn add-callback! [session matcher callback]
  "Add the given callback to the session so that if, later, a message
  matching matcher is received in session, callback will be called
  (through handle-object!)."
  ; XXX I think this needs to be rewritten due to my removal of
  ; WorldSession.  For example, there's no longer a .getPayload method
  ; anywhere.
  (let [callbacks-ref (@(.getPayload session) :callbacks)]
    (dosync
     (let [new-callback
	   (if (@callbacks-ref matcher)
	     ; Add the new callback after any existing ones
	     #(do-or (@callbacks-ref matcher)
		     callback)
	     callback)]
       (alter callbacks-ref assoc matcher new-callback)))))

(defn dvtp-send! [#^NetSession session message]
  "Send message in session."
  (io!
   (.add (.getNetOutQueue session)
         message)))

(defmacro async-call! [session [assign-var message] & code]
  "Send message in session, asynchronously bind the result to
  assign-var, and call code.  Side effects: calls add-callback! with
  session.

  TODO optional timeout parameters: seconds to wait, and code to call
  if timed out."
  `(do
     (add-callback! ~session
		    ~(lookup-response message)
		    (fn (~assign-var) ~@code))
     (dvtp-send! ~session ~message)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Convenience constructors for DVTP objects

(defn pos-to-moveseq
  "Creates a stationary MoveSeq for the given coordinates, which may
  be given as three arguments, or as a list of three numbers, or two
  lists, the second one being four numbers defining a quaternion."
  ([[x y z] [qw qx qy qz]]
     (MoveSeq. (Move/getNew (Vec. (Vector3f. (float x)
                                             (float y)
                                             (float z)))
                            (Quat. (Quaternion. (float qw)
                                                (float qx)
                                                (float qy)
                                                (float qz))))))
  ([s]
     (pos-to-moveseq s [0 0 0 1]))
  ([x y z]
     (pos-to-moveseq [x y z])))

(defn pos-quat-to-moveseq
  "Creates a stationary MoveSeq for the given coordinates and
  Quaternion."
  ([[x y z] #^Quaternion q]
     (MoveSeq. (Move/getNew (Vec. (Vector3f. x y z))
                            (Quat. q)))))

