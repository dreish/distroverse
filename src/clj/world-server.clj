
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

(def *key-to-id* (ref {})
  "Map of public keys to userid numbers.")
(def *userdata* (ref {})
  "Map of userid numbers to other account data.")

(defmacro fun-call [rform]
  `(FunCall. (.genFunCallId ~'session) ~rform))

(defmacro ac! [& args]
  "Abbreviation for (async-call! session args...)"
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
       (random)))

(defn get-position! [session]
  "Load the previous position for the given identity and add it to
  this session."
  
  )

(defn add-self-to-world! [session]
  ""
  (let [pos (get-position! session)]
    
    )
  )

(defn new-user? [id]
  "Does the given identity dict exist as a user account?"
  (not (@*key-to-id* (id :pubkey))))

(defn db-query [& args]
  "TODO turn the given syntaxey arguments into an SQL query and send it
  off to the *db* agent, guarding against the possibility that the
  server is being shut down."
  (if @*shutdown-mode*
    (throw (Exception. "db-query while in shutdown-mode"))))

(defn shutdown [t]
  "Shut down the server, and flush all database queries.  t: timeout in ms"
  (dosync
   (set-var *shutdown-mode* true)
   (send *db* #(identity nil)))
  (or (await-for t *db*)
      (throw (Exception. "Database flush timed out."))))
  
(defn setup-new-user! [session id]
  "Sets up a new user."
  (dosync
   (if (new-user? id)
     (let [new-id (get-new-userid)]
       (do
	 (alter *key-to-id* conj {(id :pubkey) new-id})
	 (alter *userdata* conj {new-id skel-user})
	 (db-query :insert :into "userdata"
		   :object (*userdata* new-id))
	 (db-query :insert :into "userids"
		   :object {:pubkey (id :pubkey) :userid new-id}))))))

(defn get-id [dvtp-id]
  (let [val (.getVal dvtp-id)]
    {:pubkey (val (Str. "pubkey"))}))

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
	  (do (if (new-user? id)
		(setup-new-user! session id))
	      (.setPayload session {:id id})
	      (add-self-to-world! session))
	  (reject-id! session))))))

(defn handle-object! [session ob]
  "Handle an object received from a proxy."
  )
