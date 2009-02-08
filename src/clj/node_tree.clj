
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


; Generate a universe node tree from a random seed

(ns node-tree
  (:use server-lib
        clojure.contrib.def
        durable-maps
        bigkey-dm))

(import '(com.jme.math Quaternion Vector3f))

(defvar *id-to-node* (dm-get-map "id-to-node")
  "Maps node IDs to nodes.")

(defn is-within
  ""
  []
  )

(defn nodes-within
  "Return a seq of IDs of nodes contained entirely within the area
  given as a vector offset relative to a given node, and a spherical
  radius.  An optional fourth argument gives a node ID of one of the
  children of rel-node-id, or the parent node, that should not be
  searched."
  ([rel-node-id #^Vector3f pos radius]
     ; XXX yuck
     (let [pnode-id (parent-node rel-node-id)]
       (lazy-cat
        (if pnode-id
          (nodes-within pnode-id
                        (translate-pos pos rel-node-id pnode-id)
                        radius
                        rel-node-id))
        (if (< (+ (vec-abs pos) radius)
               (radius-of-node rel-node-id))
          (list rel-node-id))
        (mapcat #(nodes-within %
                               (translate-pos pos rel-node-id %)
                               radius
                               pnode-id)
                (filter #(not (is-outside % pos radius))
                        (children rel-node-id))))))
  ([rel-node-id #^Vector3f pos radius exclude-id]
     ))
