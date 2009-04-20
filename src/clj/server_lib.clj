
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
        node-tree
        dvtp-lib
        clojure.contrib.def))

(import '(com.jme.math Quaternion Vector3f))
(import '(org.distroverse.dvtp Quat Vec Move MoveSeq AskInv ReplyInv
                               GetCookie Cookie FunCall FunRet))
(import '(org.distroverse.core.net NetSession))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Miscellaneous general functions

(let [rng (java.security.SecureRandom.)]
  (defn sec-random []
    "Get a pseudorandom 32-bit int.  FIXME seed with secure random
  bytes, such as from /dev/urandom."
    (locking rng
      (.nextInt rng))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

; Communications functions

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

(defn node-to-addobject
  "Turns a node hash into an AddObject object."
  [nh]
  ; XXX
  )

(defn noderef-encode
  "Turns a node hash into a DNodeRef object."
  [nh]
  ; XXX
  ; XXX how to noderef to an ephem node? (this)  Will I need to change
  ; the definition of DNodeRef?
  )

(defn node-encode
  "Turns a node hash into a DNode object."
  [nh]
  (DNode. (node-to-addobject nh)
          (nh :radius)
          (noderef-encode nh)
          (noderef-encode (parent-of nh))
          (into-array (map noderef-encode )))) ; children

(defmulti dvtp-convert
  "Convert a string or number into a Str, ULong, or Flo, pass through
  a list starting with a Dvtp class, and throw an exception for
  anything else."
  (fn [x] (class x)))

;; dvtp-wrap

(defmethod dvtp-convert clojure.lang.PersistentList
  [x]
  (if (message-set (first x))
    x
    (throw (Exception. (str "Cannot convert list " x
                            ", try a vector")))))

(defmethod dvtp-convert Integer [x]
  `(Ulong. ~x))

(defmethod dvtp-convert Boolean [x]
  (if x `(True.) `(False.)))

(defmethod dvtp-convert String [x]
  `(Str. ~x))

(defmethod dvtp-convert Double [x]
  `(Flo. (float x)))

(defn dvtp-wrap
  "Converts a sequence including strings, booleans, and numbers into a
  sequence including Strs, Bools, ULongs, and Flos."
  [s]
  (map dvtp-convert s))

