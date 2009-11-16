
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
  (:require [durable-maps :as dm])
  (:require [bigkey-dm :as bk])
  (:use util
        dvtp-lib
        prng-feedback
        matrix
        clojure.contrib.def))

(import '(com.jme.math Quaternion Vector3f))
(import '(org.distroverse.dvtp Quat Vec Move MoveSeq AskInv ReplyInv
                               GetCookie Cookie FunCall FunRet
                               AddObject ULong DNode))
(import '(org.distroverse.core.net NetSession))
(import '(org.distroverse.distroplane.lib BallFactory))

(dm/startup! :sql "dm" "dm" "nZe3a5dL")

(declare parent-of
         gen-children
         get-node)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Miscellaneous general functions

(let [rng (java.security.SecureRandom.)]
  (defn sec-random []
    "Get a pseudorandom 32-bit int.  FIXME seed with secure random
  bytes, such as from /dev/urandom."
    (locking rng
      (.nextInt rng))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Communications functions

(defn add-callback! [session matcher callback]
  "Add the given callback to the session so that if, later, a message
  matching matcher is received in session, callback will be called
  (through handle-object!)."
  (let [callbacks-ref (@(.getAttachment session) :callbacks)]
    (dosync
     (let [new-callback
           (if-let [existing-callback (@callbacks-ref matcher)]
             ;; Add the new callback after any existing ones
             #(do-or (existing-callback)
                     (callback))
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
  `(let [msg# ~message
         sess# ~session]
     (io!)
     (add-callback! sess#
                    (lookup-response-func msg#)
                    (fn [~assign-var] ~@code))
     (dvtp-send! sess# msg#)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Node-tree operations

(defvar ws-ns "ws-a/"
  "World server namespace")

(defvar- *id-to-node* (dm/get-map (str ws-ns "node-tree/id-to-node"))
  "Maps node IDs to nodes.")

(defvar- *node-tree-vars* (dm/get-map (str ws-ns "node-tree/vars"))
  "Durable variables.")

(defn get-ws-var
  "Returns the durable var with the given (keyword) name."
  [k]
  (:v (*node-tree-vars* k)))

(defn serial-no-var
  "Gets and increments a *node-tree-vars* entry."
  [k]
  (:v (dm/update *node-tree-vars* k inc-in :v)))

(defn pick-nodeid
  "Returns a new, unused node id.  Requires a dmsync transaction."
  []
  (serial-no-var :next-nodeid))

(def +zero-vec+ (Vector3f. 0 0 0))

(defn get-node
  "Returns the node with the given node id.  Requires a dmsync
  transaction."
  ([id]
     (*id-to-node* (normint id))))

(defn delete-node
  "Deletes the node with the given node id.  Requires a dmsync
  transaction."
  ([id]
     (dm/delete *id-to-node* (normint id))))

(defn selock-node
  "Returns the node with the given node id, ensuring that it does not
  change throughout the current transaction.  Requires a dmsync
  transaction."
  ([id]
     (dm/selock *id-to-node* (normint id))))

(defn add-node
  "Add the given node to the node tree.  Requires a dmsync transaction."
  ([n]
     (dm/insert *id-to-node*
                (assoc n :nodeid
                       (normint (n :nodeid))))))

(defn new-object
  "Create a new node with the given shape and add it to the node tree,
  with no parent, returning its new node id.  Requires a dmsync
  transaction."
  ([sh]
     (let [nid (pick-nodeid)]
       (add-node {:nodeid nid
                  :shape sh
                  :depth 0
                  :radius (.getRadius sh)})
       nid)))

(def get-radius :radius)

(def get-move :move)

(defn vec-abs
  ([#^Vector3f v]
     (.length v)))

(defn get-xform
  [n t]
  (-> n get-move (.transformAt t)))

(defn cur-xform
  [n]
  (get-xform n (now)))

(def ephem? :ephemeral)

(def room? :room)

(def node-depth :depth)

(defn-XXX node-pos
  "Returns the position of the given node at time (time)."
  [n]
  )

(defn parent-of
  "Returns the parent node of the given node.  Requires a dmsync
  transaction."
  [n]
  (or (if (n :parent)
        (get-node (n :parent)))
      (n :parent-ref)))

(defn children-of
  "Returns a lazy seq of the immediate children of the given node."
  [n]
  (if (n :echildren)
    (gen-children n)
    (map get-node (n :children))))

(defn root-of
  "Returns the root node of the given node.  Requires a dmsync
  transaction."
  [n]
  (if-let [p (parent-of n)]
    (recur p)
    n))

(defn parent-chain
  "Returns a seq of the ancestors for the given node.  Requires a
  dmsync transaction."
  [n]
  (lazy-seq
    (if-let [p (parent-of n)]
      (cons p
            (parent-chain p)))))

(defn search-nodes
  "Traverses nodes connected to the given start node, returning a lazy
  sequence of search results, using the given conditional functions,
  called with the node in question and a transformation matrix to
  convert a vector relative to start-node to a vector relative to the
  given node ending in the same location.  include? should return true
  if the given node is included in the search results.  ascend? should
  return true if the given node should be traversed from a child node;
  i.e., if any relative of the given node or the node itself might be
  included in the results (this should almost always be true).
  descend? should return true if the given node or any of its
  descendents might be in the search results."
  ;; Apologies for the complexity, but this is a complex problem.
  ([start-node include? descend? ascend?]
     (search-nodes start-node include? descend? ascend?
                   nil +Midentity+))
  ([start-node include? descend? ascend? exclude-node transformer]
     (let [ch (filter #(and %
                            (not= exclude-node %)
                            (descend? % (M* transformer
                                            (invert (cur-xform %)))))
                      (children-of start-node))
           pa (parent-of start-node)]
       (lazy-cat
         (if (and (not= start-node exclude-node)
                  (include? start-node transformer))
           (list start-node))
         (mapcat #(search-nodes % include? descend? ascend? start-node
                                ;; Seems unfortunate to repeat this:
                                (M* transformer
                                    (invert (cur-xform %))))
                 ch)
         (if (and pa
                  (not= pa exclude-node))
           (let [parent-transformer
                   #(M* (cur-xform %) transformer)] ;???
             (if (ascend? pa parent-transformer)
               (search-nodes pa include? descend? ascend? start-node
                             parent-transformer))))))))

(defn is-within
  "Returns a function of two args, a node and a transformation
  function, that will return logical true for nodes that lie entirely
  within the sphere defined by the given position and radius."
  [pos radius]
  (fn [node transformation]
    (let [my-pos (M* transformation pos)
          node-radius (get-radius node)
          distance (vec-abs my-pos)
          covered-radius (- radius distance)]
      (> covered-radius node-radius))))

(defn is-intersecting
  "Returns a function of two args, a node and a transformation
  function, that will return logical true for nodes that overlap the
  sphere defined by the given position and radius."
  [pos radius]
  (fn [node transformation]
    (let [my-pos (M* transformation pos)
          node-radius (get-radius node)
          distance (vec-abs my-pos)
          reach (+ radius node-radius)]
      (> reach distance))))

(defn is-containing
  "Returns a function of two args, a node and a transformation
  function, that will return logical true for nodes that overlap the
  sphere defined by the given position and radius."
  [pos radius]
  (fn [node transformation]
    (let [my-pos (M* transformation pos)
          node-radius (get-radius node)
          distance (vec-abs my-pos)
          covered-radius (- node-radius distance)]
      (> covered-radius radius))))

(defn smallest-node
  "Returns the node with the smallest size from the given seq of
  nodes."
  [node-seq]
  (min-by get-radius node-seq))

(defn nodes-within
  "Returns a seq of IDs of nodes contained entirely within the area
  given as a vector offset relative to a given node, and a spherical
  radius."
  [rel-node-id #^Vector3f pos radius]
  (search-nodes
    (get-node rel-node-id)
    (is-within pos radius)
    (is-intersecting pos radius)
    (constantly true)))

(defn nodes-within-and-under
  "Returns a seq of IDs of nodes contained entirely within the area
  given as a vector offset relative to a given node, and a spherical
  radius, that are children of or equal to that node."
  [rel-node-id #^Vector3f pos radius]
  (search-nodes
    (get-node rel-node-id)
    (is-within pos radius)
    (is-intersecting pos radius)
    (constantly false)))

(defn nodes-touching
  "Returns a seq of IDs of nodes contained partially or entirely
  within the area given as a vector offset relative to a given node,
  and a spherical radius."
  [rel-node-id #^Vector3f pos radius]
  (search-nodes
    (get-node rel-node-id)
    (is-intersecting pos radius)
    (is-intersecting pos radius)
    (constantly true)))

(defn smallest-room-containing
  "Returns the smallest room node containing the given node."
  [node-id]
  (let [node (get-node node-id)
        containing? (is-containing +zero-vec+ (get-radius node))
        match? #(and (room? %1)
                     (containing? %1 %2))
        candidate-seq (search-nodes
                        node
                        match?
                        match?
                        (constantly true))]
    (smallest-node candidate-seq)))

(defn node-tree-seq
  "Return a NON-LAZY seq of all the descendants of the node with the
  given ID."
  ([nodeid]
     (cons nodeid
           (mapcat (comp node-tree-seq :nodeid)
                   (children-of (get-node nodeid))))))

(defn replace-subnode-with-new
  "Replace whatever was in the subnode at index idx of parent node p
  with the given new-node, which should already be in the node tree."
  [p idx new-node move]
  (do
    (dm/update *id-to-node*
               (p :nodeid)
               assoc-in [:children idx]
               (new-node :nodeid))
    (dm/update *id-to-node*
               (new-node :nodeid)
               assoc-in [:parent] (p :nodeid))))

(defn convert-to-concrete
  "Takes an ephemeral node and returns a structure that can be used as
  a concrete node (without adding it to the node tree)."
  ([e]
     (assoc e
       :nodeid (pick-nodeid)
       :ephemeral false)))

(defn concretize-children
  "Replaces the :echildren column of the given node id with
  :children."
  ([nodeid]
     (let [n (selock-node nodeid)
           _ (when-not (n :echildren)
               (throw (Exception. (str "Node " nodeid " does not have"
                                       " ephemeral children"))))
           ech (children-of n)
           cch (map convert-to-concrete ech)
           new-children (map :nodeid cch)]
       (doall (map add-node cch))
       (dm/update *id-to-node*
                  nodeid
                  assoc
                  :echildren nil
                  :children (vec new-children)))))

(defn maybe-concretize-children
  "Calls concretize-children if the node with the given id has
  ephemeral children.  Requires a dmsync transaction."
  ([nodeid]
     (when (:echildren (get-node nodeid))
       (concretize-children nodeid))))

;;; Kill these two?  Rewrite?  Useful at all?
(defn-XXX make-concrete
  ([node]
     ))
(defn-XXX add-subnode
  "Add node c as a child of node p, with move m.  Returns the new node
  ID."
  [p m c]
  (let [p (if (ephem? p)
            (make-concrete p)
            p)]
    ))

(defn reparent
  "Makes the node with the given ID a child of the given new-parent,
  with move move.  Returns the given node id.  Requires a dmsync
  transaction."
  [new-parent move node]
  (do
    (maybe-concretize-children new-parent)
    (let [np (and new-parent (get-node new-parent))
          nd (if np (-> np :depth inc)
                    1)]
      (when (and new-parent (not np))
        (throw (Exception. (str "reparent: new parent node " new-parent
                                " does not exist"))))
      (when np
        (dm/update *id-to-node* new-parent assoc
                   :children (conj (np :children)
                                   node)))
      (dm/update *id-to-node* node assoc
                 :parent new-parent
                 :depth nd
                 :move move))))

(defn add-object
  "Adds shape as a child of node, with move move.  Returns the new node
  ID.  Requires a dmsync transaction."
  [node move shape]
  (let [nob (new-object shape)]
    (reparent node move nob)
    nob))

(defn node-encode
  "Turns a node hash into a DNode message."
  [nh]
  (DNode. (node-to-addobject nh)
          (nh :radius)
          (noderef-encode nh)
          (noderef-encode (parent-of nh))
          (into-array (map (comp noderef-encode get-node)
                           (nh :children)))
          (nh :depth)))

(defn rel-vector
  "Returns a vector from node a to node b.  Throws an error if a and b
  turn out not to be in the same tree."
  ([a b]
     (rel-vector a b +Midentity+ +Midentity+))
  ([a b a->root root->b]
     (cond (or (nil? a)
               (nil? b))
             (throw (Exception. (str "rel-vector called on two nodes"
                                     " in unconnected trees")))
           (= a b)
             (M* a->root root->b +zero-vec+)
           :else
             (if (< (node-depth a)
                    (node-depth b))
               (recur a
                      (parent-of b)
                      a->root
                      (M* (cur-xform b) root->b))
               (recur (parent-of a)
                      b
                      (M* a->root (invert (cur-xform a)))
                      root->b)))))


;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Ephemeral node generation functions

(defn subseed
  "Return a new seed for the node described by parent and
  subnode-index."
  [parent subnode-index]
  (let [pshift   (parent :seed-mutation-shift)
        sshift   (-> pshift (+ 4) (rem 59))
        pseed    (parent :seed)
        mutation (bit-shift-left (+ 1 subnode-index) sshift)
        seedseed (bit-xor pseed mutation)]
    (long (first (feedback-long-seq seedseed)))))

(defn lognormal-rand
  "Takes a normal sequence and a couple of base-e scale
  parameters (and an optional ln max) and returns a lognormal
  sequence."
  ([randseq log-avg log-std-dev]
     (map #(-> % (* log-std-dev) (+ log-avg) Math/exp float)
          randseq))
  ([randseq log-avg log-std-dev log-max]
     (if log-max
       (let [max (float (Math/exp log-max))]
         (map #(if (> % max) max %)
              (lognormal-rand randseq log-avg log-std-dev)))
       (lognormal-rand randseq log-avg log-std-dev))))

(defn pick-radius [spec seed]
  (first
   (apply lognormal-rand (feedback-normal-seq seed)
          (map spec '(:log-avg-size :log-std-dev :log-max-size)))))

(defn random-quat
  "Returns a uniformly-distributed random orientation, as a
  Quaternion."
  [seed]
  (let [[x y z] (take 3 (feedback-normal-seq seed))
        rot (first (feedback-float-seq (inc seed)))
        theta (* rot 2 Math/PI)
        vec (if (= 0 x y z)
              (Vector3f. 1 1 1)
              (Vector3f. x y z))]
    (doto (Quaternion.)
      (.fromAngleAxis theta vec))))

(defn- new-gen-node
  "Return a new ephemeral node for the given parameters."
  [parent depth spec lspec subnode-index seed r moveseq spec-name]
  (let [seed-mut-shift (-> (parent :seed-mutation-shift)
                           (+ 4)
                           (rem 59))]
    {:name (lspec :name)
     :generator (lspec :generator)
     :layer (lspec :layer)
     :radius r
     :seed seed
     :moveseq moveseq
     :ephemeral true
     :seed-mutation-shift seed-mut-shift
     :depth depth
     :parent-ref parent
     :parent (parent :nodeid)           ; might be nil
     :echildren (list `gen-echildren spec-name r (lspec :layer)
                      depth seed seed-mut-shift nil)
     ;; XXX need the path from a concrete node here?
     }))

(defn new-top-gen-node
  "Returns a new highest-level node for a given layer spec."
  [parent depth spec lspec pos subnode-index spec-name]
  (let [seed (subseed parent subnode-index)
        r    (pick-radius lspec (inc seed))]
    (new-gen-node parent depth spec lspec subnode-index seed r
                  (pos-quat-to-moveseq pos (random-quat (+ seed 2)))
                  spec-name)))

(defn new-sub-gen-node
  "Returns a new highest-level node for a given layer spec."
  [parent depth spec lspec pos subnode-index r spec-name]
  (let [seed (subseed parent subnode-index)]
    (new-gen-node parent depth spec lspec subnode-index seed r
                  (pos-to-moveseq pos)
                  spec-name)))

(defn pseudorandom-pos
  "Generate a reproduceable pseudorandom location for a new subnode.
  Returns coordinates as a sequence: (x y z)."
  [coord-scalars [unused-size offset-factor rand-factor]
   skews radius prngs]
  (let [offset (* offset-factor radius)
        rand-scale (* rand-factor radius)]
    (map (fn [rng scalar skew]
           (-> offset (* scalar skew)
                      (+ (* rng rand-scale))
                      (- (/ rand-scale 2))))
         prngs
         coord-scalars
         skews)))

(defvar- ntvars (dm/dmsync (dm/get-map (str ws-ns "node-tree/vars"))))

(defn gen-echildren
  [spec-dm-varname par-radius par-layer par-depth par-seed
   par-seed-mut-shift par-id]
  (let [spec (dm/dmsync (:v (ntvars spec-dm-varname)))
        fakeparent {:radius par-radius
                    :layer par-layer
                    :depth par-depth
                    :seed par-seed
                    :seed-mutation-shift par-seed-mut-shift
                    :nodeid par-id}
        generator (resolve (:generator (spec par-layer)))]
    (generator fakeparent spec par-layer spec-dm-varname)))

(defn gen-fractalplace
  "Returns a sequence of 8 subnodes for the given parent node."
  ([parent spec spec-name]
     (let [parent-layer (parent :layer)]
       (gen-fractalplace parent spec parent-layer spec-name)))
  ([parent spec parent-layer spec-name]
     (let [parent-rad   (parent :radius)
           layer-spec   (spec parent-layer)
           structure    (layer-spec :structure)
           size-factor  (structure 0)
           my-radius    (* size-factor parent-rad)
           next-layer?  (< my-radius (layer-spec :subscale))
           depth        (inc (parent :depth))
           subnode-gen
           (if next-layer?
             #(new-top-gen-node parent depth spec
                                (spec (inc parent-layer))
                                %1 %2 spec-name)
             #(new-sub-gen-node parent depth spec
                                layer-spec
                                %1 %2 my-radius spec-name))]
       (map subnode-gen
            (map pseudorandom-pos
                 (for [xo [-1 1] yo [-1 1] zo [-1 1]]
                   (list xo yo zo))
                 (repeat (or (parent :dim-skew) [1 1 1]))
                 (repeat structure)
                 (repeat parent-rad)
                 (partition 3 (feedback-float-seq (parent :seed))))
            (range 1 9)))))

(defn gen-children
  "Return a lazy seq of the ephemeral children of the given node."
  ([n]
     (let [[fname & fargs] (n :echildren)]
       (apply (resolve fname)
              fargs))))

(defn-XXX gen-starsystem
  ""

  )

(defn gen-simple-starsystem
  "Returns a sequence of subnodes making up the given star system."
  ([parent spec parent-layer spec-name]
     (list
      {:name "star"
       :radius 7e9
       :moveseq (pos-to-moveseq [0 0 0])
       :ephemeral true
       :shape (sphere :rows 4 :radius 7e9)
       :depth (inc (parent :depth))
       :parent-ref parent
       :parent (parent :nodeid)
       })))

(defn- check-structure
  "Throws an exception if the structure constants would violate the
  rule that all subnodes of a parent node must fit within the parent
  node."
  [[size offset randfactor :as s] name]
  (let [totaloffset (+ offset (/ randfactor 2))
        maxdistance (+ (Math/sqrt (* totaloffset totaloffset 3))
                       size)]
    (if (> maxdistance 1.0)
      (throw (Exception. (str name " structure too big by a factor of "
                              maxdistance ", try "
                              (with-out-str
                               (prn (map #(/ % maxdistance) s)))))))))

(defn new-universe-spec
  "Returns a seq containing the given layer specs, each one a hash,
  adding a :subscale key in each spec that gives the size threshhold
  at which the next layer should be used (or 0 in the last layer
  spec)."
  [& layer-specs]
  (do
    (dorun (map #(if (= gen-fractalplace (% :generator))
                   (check-structure (% :structure) (% :name)))
                layer-specs))
    (into []
          (map #(assoc (first %1)
                  :subscale (if (second %1)
                              (* (Math/exp (:log-max-size (second %)))
                                 ((:structure (first %1)) 0))
                              0)
                  :layer %2)
               (rests layer-specs)
               (iterate inc 0)))))

(defn new-universe
  "Generate a new top node for the given universe-spec and random
  seed.  N.B.: returns an ephemeral node."
  [spec seed]
  (new-gen-node {:seed-mutation-shift 0}
                (first spec)
                0
                seed
                (pick-radius (spec 0) (inc seed))
                (pos-to-moveseq [0 0 0])))

(defvar big-universe-spec
  (new-universe-spec
   {:name          "universe",
    :generator     'gen-fractalplace,
    :log-max-size  60.56,            ; ~ 21.14 bln light years
    :log-avg-size  60.56,
    :log-std-dev   0,
    :structure     [0.4 0.2 0.25],   ; Each subnode is 2/5 parent's
                                     ; size, offset is 1/5 parent's
                                     ; radius + random factor of up to
                                     ; 1/4 * parent's radius.
    }

   {:name          "supercluster",
    :generator     'gen-fractalplace,
    :log-max-size  56.87148,         ; ~ 528.5 mln light years
    :log-avg-size  55.955,           ; ~ 211.36 mln light years
    :log-std-dev   1.0,
    :structure     [0.44 0.19 0.24], ; Denser than above
    }

   {:name          "cluster",
    :generator     'gen-fractalplace,
    :log-max-size  54.08633,         ; ~ 32.62 mln light years
    :log-avg-size  52.93494,         ; ~ 10.31 mln light years
    :log-std-dev   0.8,
    :structure     [0.47 0.188 0.235], ; Denser still
    }

   {:name          "galaxy",
    :generator     'gen-fractalplace,
    :log-max-size  49.48105,         ; ~ 326,200 light years
    :log-avg-size  47.17847,         ; ~ 32,620 light years
    :log-std-dev   1.3,
    :rot-axis      [1 0 1],
    :avg-rot-speed 8.732015e-16      ; radians per second
    :dim-skew      [1 0.1 1],        ; y-dim is 1/10 x- and z-dims
    :structure     [0.43439 0.21769 0.21769],
    }

   {:name          "starsystem",
    :generator     'gen-starsystem,
    :log-max-size  38.39528,         ; ~ 5 light years
    :log-avg-size  38.39528,
    :log-std-dev   0,
    })
  "Parameters defining how universes are generated.")

(defvar small-universe-spec
  (new-universe-spec
   {:name          "stellarcluster",
    :generator     'gen-fractalplace,
    :log-max-size  40.0,             ; ~24.88 light years
    :log-avg-size  40.0,
    :log-std-dev   0,
    :structure     [0.43457 0.21763 0.21763],
    }
   
   {:name          "starsystem",
    :generator     'gen-simple-starsystem,
    :log-max-size  38.39528,         ; ~ 5 light years
    :log-avg-size  38.39528,
    :log-std-dev   0,
    }
   )
  "Parameters for a small, simple universe of just a few polyhedral
  stars in an open cluster.")
