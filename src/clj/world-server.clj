
;; <copyleft>

;; Copyright 2008 Dan Reish

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
;; library), containing parts covered by the terms of the Common
;; Public License, the licensors of this Program grant you additional
;; permission to convey the resulting work. {Corresponding Source for
;; a non-source form of such a combination shall include the source
;; code for the parts of clojure-contrib used as well as that of the
;; covered work.}

;; </copyleft>

(import '(org.distroverse.dvtp Str Err))
(use 'server-lib)
(use 'clojure.contrib.def)
(use 'durable-maps)
(use 'bigkey-dm)

(defvar *key-to-id* (dm-get-map "key-to-id")
  "Map of public keys to userid numbers")
(defvar *userdata* (dm-get-map "userdata")
  "Map of userid numbers to other account data")
(defvar *avatars* (ref #{})
  "Set of all avatars")
(defvar *id-to-node* (dm-get-map "id-to-node")
  "Maps node IDs to nodes.")

(defn gen-fun-call-id [session]
  "Return a fun-call serial number unique within the given session."
  (dosync
   (let [ret @(session :funcall-counter)]
     (alter (session :funcall-counter) (inc ret))
     ret)))

(defmacro fun-call [rform]
  "Generates a FunCall constructor.  Assumes 'session is bound to a
  session."
  `(FunCall. (gen-fun-call-id ~'session) ~@rform))

(defmacro ac! [& args]
  "Abbreviation for (async-call! session args...).  Assumes 'session
  is bound to a session."
  `(async-call! ~'session ~@args))

(defn valid-id? [id challenge response]
  "Is the given id valid?  XXX implement this"
  true)

(defn gen-id-challenge [id session]
  "Generate a challenge string for the given id and session."
  (str (.get id (Str. "preferred-name"))
       " @ "
       (.getPeerAddress session)
       " t "
       (gmt-time-string)
       " # "
       (sec-random)))

(defn get-node [nodeid]
  "Return the node with the given id (a serial number)."
  (dm-dosync
   (*id-to-node* nodeid)))

(defn add-object [node move shape]
  "Add shape as a child of node, with move move."
  (dosync
   ; XXX
  ))

(defn add-self-to-world [att session]
  "Add the session's avatar to the world."
  (let [userid (att :userid)
	pos (att :lastpos)]
    (dosync
     (let [avatar (add-object (get-node (pos :node))
			      (pos :move)
			      (att :avatarshape))]
       (commute *avatars* conj {:object avatar :session session})
       (alter att assoc :avatar avatar)))))

(defn new-user? [id]
  "Does the given identity dict exist as a user account?"
  (not (dm-dosync (*key-to-id* (id :pubkey)))))

(defn shutdown! [t]
  "Shut down the server, and flush all database queries.  t: timeout in ms"
  (disable-server-listener)
  (disconnect-users)
  (dm-shutdown!))

(defn skel-user [id userid]
  "Return the skeleton user account, with the given ID"
  (let [skel-userid (get-userid-by-name "skel")]
    (assoc (*userdata* skel-userid)
      :pubkey (id :pubkey)
      :userid userid)))

(defn setup-new-user! [session att id]
  "Sets up a new user."
  (dm-dosync
   (if (new-user? id)
     (let [new-userid (get-new-userid)]
       (do
	 (dm-insert *key-to-id* {(id :pubkey) new-userid})  ; XXX problem
	 (dm-insert *userdata* {new-userid (skel-user id new-userid)})
	 (alter att assoc :userid new-userid)
	 (db-run :insert :into "userdata"
		 :object (@*userdata* new-userid))
	 (db-run :insert :into "userids"
		 :object {:pubkey (id :pubkey)
			  :userid new-userid}))))))

(defn get-id [dvtp-id]
  (let [val (.getVal dvtp-id)]
    {:pubkey (val (Str. "pubkey"))}))

(defn new-session-attachment [session id]
  "Return a new session attachment object."
  (ref {:callbacks       (ref {})
	:session         session
	:id              id
	:funcall-counter (ref 0)}))

(defn init-connection! [session token]
  "Performs basic new-connection setup: getting and verifying the
  user's identity, looking up the user's position, and adding the
  user's avatar to the visible world.  Side effects: plenty, including
  the use of async-call! of course, as well as actually putting the
  user into the world, and possibly even setting up the new user
  identity."
  (ac! [id-reply (AskInv. "ID" "id")]
    (let [id (get-id id-reply)
	  challenge (gen-id-challenge id session)]
      (ac! [id-response (fun-call ("challenge" "id" challenge))]
	(if (valid-id? id challenge id-response)
	  (let [att (new-session-attachment session id)]
	    (do
	      (.setAttachment session (class att) att)
	      (if (new-user? id)
		(setup-new-user! session att id))
	      (add-self-to-world att session)))
	  (reject-id! session))))))

(defn handle-callback! [session att ob]
  "Look for the given ob in the given session's callback map, and if
  it is found, call the callback with the object.  If the callback
  returns true, remove it from the map and return true.  Otherwise,
  return false."
  (let [matcher   (trim-to-matcher ob)
	callbacks (@att :callbacks)]
    (if-let callback! (@callbacks matcher)
      (if (callback!)
	(dosync
	 (alter callbacks dissoc matcher)
	 true)
	false))))

(defn handle-standard! [session att ob]
  "Handle the given message using a standard, fixed code map, and
  return true."
  ; XXX something with multimethods?
  )

(defn handle-object! [session att ob]
  "Handle an object received from a proxy.  TODO log unhandled messages?"
  (or (handle-callback! session att ob)
      (handle-standard! session att ob)))


(defn handle-location [noq loc]
  "Handle an anonymous LOCATION request."
  (.offer noq (Str. "http://www.distroverse.org/proxies/WorldProxy.jar"
		    ".*"
		    "org.distroverse.proxy.WorldProxy")))

(defn handle-get [noq url]
  "Handle an anonymous GET request."
  (.offer noq (Err. "No GET resources at this site" 404)))
