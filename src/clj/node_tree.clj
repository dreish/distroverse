
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


; Generate a universe node tree from a random seed

(ns node-tree
  (:use server-lib
        clojure.contrib.def
        durable-maps
        bigkey-dm
        def-universe
        util))

(import '(com.jme.math Quaternion Vector3f))

(defvar *id-to-node* (dm-get-map "id-to-node")
  "Maps node IDs to nodes.")

(def zero-vec (Vector3f. 0 0 0))

(def get-node *id-to-node*)

(def get-radius :radius)

(defn parent-of
  "Returns the parent node of the given node"
  [n]
  (if (n :ephemeral)
    nil  ; XXX this might be hard
    (get-node (n :parent))))

(defn children-of
  "Returns a lazy seq of the immediate children of the given node."
  [n]
  (if (n :ephemeral)
    (gen-children n)
    (map get-node (n :children))))

(defn root-of
  "Returns the root node of the given node."
  [n]
  (if-let [p (parent-of n)]
    (recur p)
    n))

(defn parent-chain
  "Returns a seq of the ancestors for the given node."
  [n]
  (lazy-seq
    (if-let [p (parent-of n)]
      (cons p
            (parent-chain p)))))

(defn add-to-transformer
  "Add the position of the given node relative to its parent to the
  given vector transformation function, returning a new vector
  transformation function."
  [transformer n]
  (let [pos (node-pos n now)
        pos-xformed (transformer pos)]
    (new-transformer pos-xformed)))

(defn sub-from-transformer
  "Subtract the position of the given node relative to its parent to
  the given vector transformation function, returning a new vector
  transformation function."
  [transformer n]
  (let [pos (node-pos n now)
        pos-xformed (vec-rev (transformer (vec-rev pos)))]
    (new-transformer pos-xformed)))

(defn search-nodes
  "Traverses nodes connected to the given start node, returning a lazy
  sequence of search results, using the given conditional functions,
  called with the node in question and a transformation function to
  convert a vector relative to start-node to a vector relative to the
  given node ending in the same location.  include? should return true
  if the given node is included in the search results.  ascend? should
  return true if the given node should be traversed from a child node;
  i.e., if any relative of the given node or the node itself might be
  included in the results (this should almost always be true).
  descend? should return true if the given node or any of its
  descendents might be in the search results."
  ; Apologies for the complexity, but this is a complex problem.
  ([start-node include? descend? ascend?]
     (search-nodes start-node include? descend? ascend? nil identity))
  ([start-node include? descend? ascend? exclude-node transformer]
     (let [ch (filter #(and %
                            (not= exclude-node %)
                            (descend? % (add-to-transformer
                                           transformer %))
                      (children-of start-node))
           pa (parent-of start-node)]
       (lazy-cat
         (if (and (not= start-node exclude-node)
                  (include? start-node transformer))
           (list start-node))
         (mapcat #(search-nodes % include? descend? ascend? start-node
                                ; Seems unfortunate to repeat this:
                                (add-to-transformer transformer %))
                 ch)
         (if (and pa
                  (not= pa exclude-node))
           (let [parent-transformer
                   (sub-from-transformer transformer pa)]
             (if (ascend? pa parent-transformer)
               (search-nodes pa include? descend? ascend? start-node
                             parent-transformer))))))))

(defn is-within
  "Returns a function of two args, a node and a transformation
  function, that will return logical true for nodes that lie entirely
  within the sphere defined by the given position and radius."
  [pos radius]
  (fn [node transformation]
    (let [my-pos (transformation pos)
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
    (let [my-pos (transformation pos)
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
    (let [my-pos (transformation pos)
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
        containing? (is-containing zero-vec (get-radius node))
        match? #(and (is-room %1)
                     (containing? %1 %2))
        candidate-seq (search-nodes
                        node
                        match?
                        match?
                        (constantly true))]
    (smallest-node candidate-seq)))
