
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

(ns world-server
  (:require [durable-maps :as dm]
            [bigkey-dm    :as bk])
  (:use [clojure.contrib.def]
        [server-lib]
        [dvtp-lib]
        [util]))
(import-dvtp)

(dm/startup! :sql "dm" "dm" "nZe3a5dL")

(bk/startup!)

(dm/init!)

(bk/init!)

(defvar *key-to-id* (dm/dmsync (bk/get-map (str ws-ns "key-to-id")))
  "Map of public keys to userid numbers")
(defvar *userdata* (dm/get-map (str ws-ns "userdata"))
  "Map of userid numbers to other account data")
(defvar *avatars* (dm/get-map (str ws-ns "avatars"))
  "Set of node IDs for all in-world avatars (those having non-nil
  parents)")
(defvar *sessions* (ref #{})
  "Set of sessions for all active connections")

(defmacro satget
  "Get a field from the current session attachment."
  [field]
  `(-> ~'session .getAttachment deref ~field))

(defn gen-fun-call-id
  "Return a fun-call serial number unique within the given session."
  [session]
  (dosync
   (let [ret @(satget :funcall-counter)]
     (alter (satget :funcall-counter)
            inc)
     ret)))

(defmacro fun-call
  "Generates a FunCall constructor.  Assumes 'session is bound to a
  session."
  [& rform]
  `(FunCall. (into-array DvtpExternalizable
                  (list (ULong. (long (gen-fun-call-id ~'session)))
                        ~@(dvtp-wrap rform)))))

(defmacro dstmt!
  "Sends a DVTP FunCall statement.  Assumes 'session is bound to a
  session."
  [& rform]
  `(dvtp-send! ~'session
               (FunCall. (into-array DvtpExternalizable
                                     [ (ULong. (long 0))
                                       ~@(dvtp-wrap rform) ]))))

(defmacro ac!
  "Abbreviation for (async-call! session args...).  Assumes 'session
  is bound to a session."
  [& args]
  `(async-call! ~'session ~@args))

(defn valid-id?
  "Does the given response authenticate id against challenge?
  XXX implement this"
  [id challenge response]
  true)

(defn #^String gen-id-challenge
  "Generate a challenge string for the given id and session."
  [id session]
  "bogus challenge @ 40:20:30 # 123"
  #_(str (.get id (Str. "preferred-name"))
       " @ "
       (.getPeerAddress session)
       " t "
       (gmt-time-string)
       " # "
       (sec-random)))

(defn default-starting-pos
  []
  {:node 1
   :move (pos-to-moveseq 0 0 0)})

(defn default-avatar
  []
  (sphere {}))

(defn add-self-to-world
  "Add the session's avatar to the world."
  [att session]
  (let [userid (att :userid)
        pos (or (att :lastpos)
                (default-starting-pos))
        avatar
          (dm/dmsync
           (let [avatar                 ; FIXME This seems clumsy.
                   (reparent (pos :node)
                             (pos :move)
                             (or (att :avatarnode)
                                 (new-object (or (att :avatarshape)
                                                 (default-avatar)))))]
             (commute *sessions* conj session)
             (dm/insert *avatars* {:id (:nodeid avatar)})
             (alter att assoc
                    :avatar avatar
                    :avatar-nid (:nodeid avatar))
             avatar))]
    (tio (dstmt! "set-avatar" (ULong. (long (:nodeid avatar)))))))

(defn get-id-pubkey
  "Stub; returns the public key portion of the given ID."
  [id]
  "pubkey")

(defn new-user?
  "Does the given identity dict exist as a user account?  Must be in a
  dm transaction."
  [id]
  (not (*key-to-id* (get-id-pubkey id))))

(defn get-all-avatars
  "Returns all _in-world_ avatars (as nodeids).  Requires a dmsync
  transaction."
  []
  (dm/pkeys *avatars*))

(defn remove-avatar-node
  [nodeid]
  (do
    (dm/delete *avatars* nodeid)
    (reparent nil nil nodeid)))

(defn deparent-all-avatars
  "Removes all avatars from the world.  Requires a dmsync transaction."
  []
  (dorun
   (map #(dm/dmsync (remove-avatar-node %))
        (dorun (dm/dmsync (get-all-avatars))))))

(defn startup!
  "Performs basic initialization operations before accepting
  connections.  NB: The listener does not exist yet."
  []
  ;; FIXME Doesn't actually need ! yet, but I'm assuming it will
  (deparent-all-avatars))

(defn-XXX disable-server-listener
  ""
  []
  )

(defn-XXX disconnect-users
  ""
  []
  )

(defn shutdown!
  "Shut down the server, and flush all database queries.  t: timeout
  in ms"
  [t]
  (disable-server-listener)
  (disconnect-users)
  (dm/shutdown!))

(defn new-user-from-skel
  "Return a copy of the skeleton user account, with the given ID"
  [id userid]
  (let [skel-userid (get-ws-var :skel-userid)]
    (assoc (*userdata* skel-userid)
      :k (get-id-pubkey id)
      :id userid)))

(defn setup-new-user
  "Sets up a new user."
  [session att id]
  (dm/dmsync
   (if (new-user? id)
     (let [new-userid (get-new-userid)]
       (do
         (bk/insert *key-to-id* {:k (get-id-pubkey id),
                                 :id new-userid})
         (dm/insert *userdata* (new-user-from-skel id new-userid))
         (alter att assoc :userid new-userid))))))

(defn get-id
  [dvtp-id]
  (let [val (.getValue dvtp-id)]
    {:pubkey val}))

(defn new-session-attachment
  "Return a new session attachment object."
  [session id]
  (ref {:callbacks       (ref {})
        :session         session
        :id              id
        :detail          -10.0
        :loading         true
        :funcall-counter (ref 0)}))

(defn start-rendering
  "Begins sending an envoy objects to display."
  [att session]
  (let [parents (doall
                 (map node-encode (parent-chain (att :avatar))))]
    (tio
     (dstmt! "setprop" "loading" true)
     (pr-dup (prn parents))
     (dvtp-send! session parents))))

(defn-XXX reject-id!
  "Tells the client it failed to log in."
  []
  )

(defn init-connection!
  "Performs basic new-connection setup: getting and verifying the
  user's identity, looking up the user's position, and adding the
  user's avatar to the visible world.  Side effects: plenty, including
  the use of async-call! of course, as well as actually putting the
  user into the world, and possibly even setting up the new user
  identity."
  ([session token]
     (let [att (new-session-attachment session nil)]
       (.setAttachment session (class att) att)
       (ac! [id-reply (AskInv. "ID" (Str. "id"))]
         (let [id (get-id id-reply)
               challenge (gen-id-challenge id session)]
           (ac! [id-response (fun-call "challenge" "id" (Str. challenge))]
             (if (valid-id? id challenge id-response)
               (dm/dmsync
                 (alter att assoc :id id)
                 (if (new-user? id)
                   (setup-new-user session att id))
                 (add-self-to-world att session)
                 (start-rendering att session))
               (reject-id! session))))))))

(defn dropped-connection!
  "Performs dropped-connection cleanup."
  ([session]
     ;; Turn avatar into a statue?  Add a timeout to remove the avatar
     ;; from the world?
     nil))

(defmulti trim-to-matcher
  "Returns an object of the same class as msg, with the same values as
  msg for the fields used to correlate messages to their responses,
  and with canonical values for the other fields."
  class)

(defmethod trim-to-matcher ReplyInv
  [msg]
  (ReplyInv. #^DvtpExternalizable (.getKey msg)))

(defmethod trim-to-matcher FunRet
  [msg]
  (FunRet. (into-array DvtpExternalizable
                       [ (.getContents msg 0) ])))

(defn handle-callback!
  "Look for the given ob in the given session's callback map, and if
  it is found, call the callback with the object.  If the callback
  returns true, remove it from the map and return true.  Otherwise,
  return false."
  [session att ob]
  (let [matcher   (trim-to-matcher ob)
        callbacks (@att :callbacks)]
    (when-let [callback! (@callbacks matcher)]
      (callback! ob)
      (dosync
       (alter callbacks dissoc matcher))
      true)))

(defmulti dvtp-dispatch-val
  "Return the dispatch value for the given DVTP message object.  For
  many classes, this is just the class of the message object.  For
  others, it is a combination of the class and some subset of the
  value."
  class)

(defmethod dvtp-dispatch-val :default
  [o]
  (class o))

(defmethod dvtp-dispatch-val org.distroverse.dvtp.FunCall
  [#^org.distroverse.dvtp.FunCall o]
  [org.distroverse.dvtp.FunCall
   (str (.getContents o 0))])

(defmulti handle-standard!
  "Handle the given message using a standard, fixed code map, and
  return true.  Arguments: session, att, message-ob"
  (fn [session att ob]
    (dvtp-dispatch-val ob)))

(defmethod handle-standard! :default
  [session att ob]
  (throw (Exception.
          (str "No handle-standard! dispatch for "
               (pr-str ob)))))

(defmethod handle-standard! [org.distroverse.dvtp.FunCall
                             "dump-universe"]
  [session att ob]
  (dvtp-send! session
              (dm/dmsync (doall (map node-encode
                                     (node-tree-seq 1))))))

(defn handle-object!
  "Handle an object received from an envoy.  TODO log unhandled messages?"
  ([session att ob]
     (println (str "(handle-object! " (pr-str ob) ")"))
     (or (handle-callback! session att ob)
         (handle-standard! session att ob))))


(defn handle-location
  "Handle an anonymous LOCATION request."
  [noq loc]
  (.offer noq (EnvoySpec. "http://www.distroverse.org/envoys/WorldEnvoy.jar"
                          ".*"
                          "org.distroverse.envoy.WorldEnvoy")))

(defn handle-get
  "Handle an anonymous GET request."
  [noq url]
  (.offer noq (Err. "No GET resources at this site" 404)))
