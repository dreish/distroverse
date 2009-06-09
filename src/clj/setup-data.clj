
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

(ns setup-data
  (:require :reload-all [durable-maps :as dm])
  (:require :reload-all [bigkey-dm    :as bk])
  (:use [clojure.contrib.def]))

(dm/startup! :sql "dm" "dm" "nZe3a5dL")

; Harmless to run this if it has already been run:
(dm/init!)

(bk/init!)

(defvar ws-ns "ws-a/"
  "World server namespace")

(dm/create-map! (str ws-ns "node-tree/vars")
                {:cols {:k ["VARCHAR(32)" :keyword]
                        :v ["MEDIUMTEXT" :obj]}
                 :key :k})

(dm/create-map! (str ws-ns "node-tree/id-to-node")
                {:cols {:nodeid ["VARCHAR(32)" :obj]
                        :children ["MEDIUMTEXT" :obj]
                        :echildren ["MEDIUMTEXT" :obj]
                        :parent ["VARCHAR(32)" :obj]
                        :shape ["MEDIUMTEXT" :obj]
                        :radius ["VARCHAR(32)" :obj]
                        ; XXX
                        }
                 ; FIXME :checked-cols should be handled internally by
                 ; dm (any :seq col needs this check/fix thing), but
                 ; it's easier to put it here for now
                 ; :checked-cols `[[:echildren dm/seq-check dm/seq-fix]]
                 :key :nodeid})

(dm/dmsync
 (dm/insert (dm/get-map (str ws-ns "node-tree/vars"))
            {:key :small-universe-spec-1
             :val small-universe-spec}))

(dm/dmsync
 (dm/insert (dm/get-map (str ws-ns "node-tree/id-to-node"))
            {:nodeid 1
             :parent nil
             :echildren '(gen-echildren :small-universe-spec-1
                                        #=(eval (Math/exp 40.0))
                                        0
                                        1)
             :radius #=(eval (Math/exp 40.0))
             }))

