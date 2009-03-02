
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

(use :reload-all 'durable-maps)

(use :reload-all 'util)

(defn setup-dm-test-data!
  []
  (dm-create-new-map!
   "dm-stress-tests/sum-counter"
   {:cols {:k ["VARCHAR(32)" :str]
           :v ["TEXT" :obj]}
    :key :k})
  (dm-create-new-map!
   "dm-stress-tests/scattershot"
   {:cols {:k ["VARCHAR(32)" :obj]}
    :key :k}))

(setup-dm-test-data!)

(def sum-counter-dm (dm-get-map "dm-stress-tests/sum-counter"))

(def scattershot-dm (dm-get-map "dm-stress-tests/scattershot"))

(defn sum-counter-init-table
  []
  (dm-dosync
   (dm-delete sum-counter-dm "counter")
   (dm-delete sum-counter-dm "sum")
   (dm-insert sum-counter-dm {:k "counter" :v 0})
   (dm-insert sum-counter-dm {:k "sum" :v 0})))

(defn add-and-get
  [dm row n]
  (dm-dosync
   ((dm-update dm row update-in [:v] #(+ n %)) :v)))

(defn sum-counter-loop-to
  "Loop for one thread of the sum-counter-stress-test."
  [count-to my-thread-id]
  (loop []
    (if ;(dm-dosync
         (let [counter (add-and-get sum-counter-dm "counter" 1)]
           (if (<= counter count-to)
             (let [sum (add-and-get sum-counter-dm "sum" counter)]
;               (println "added" counter "in thread" my-thread-id
;                        "and got" sum)
               true)
             false)) ;)
      (recur)))
  (if (zero? my-thread-id)
    (prn "Finished at" (System/nanoTime))))

(defn sum-counter-stress-test
  "Tests highly contentious synchronized updates."
  [count-to n-threads]
  (do
    (sum-counter-init-table)
    (prn "Started at" (System/nanoTime))
    (dotimes [t n-threads]
      (.start (Thread.
               #(sum-counter-loop-to count-to t))))))

(defn scattershot-init-table
  [n]
  (dm-dosync
   (dotimes [i n]
     (dm-delete scattershot-dm i))))

(defn scattershot-add-loop
  [n-to-add added my-thread-id]
  (loop []
    (if (dm-dosync
         (if (< @added n-to-add)
           (let [r (int (rand n-to-add))]
             (when-not (scattershot-dm r)
               (dm-insert scattershot-dm {:k r})
               (commute added inc))
             true)
           false))
      (recur))))

(defn scattershot-del-loop
  [n-to-add added my-thread-id]
  (loop []
    (if (dm-dosync
         (if (> @added 0)
           (let [r (int (rand n-to-add))]
             (when (scattershot-dm r)
               (dm-delete scattershot-dm r)
               (commute added dec))
             true)
           false))
      (recur))))

(defn scattershot-stress-test
  "Tests insertion by several threads, and deletion by several threads."
  [n-rows n-threads]
  (let [added (ref 0)]
    (scattershot-init-table n-rows)
    (prn "Started at" (System/nanoTime))
    (dotimes [t (dec n-threads)]
      (.start (Thread.
               #(scattershot-add-loop n-rows added t))))
    (scattershot-add-loop n-rows added (dec n-threads))
    (prn "Finished adding at" (System/nanoTime))
    (dm-dosync
     (dotimes [i n-rows]
       (assert (scattershot-dm i))))
    (prn "Finished checking rows added at" (System/nanoTime))
    (dotimes [t (dec n-threads)]
      (.start (Thread.
               #(scattershot-del-loop n-rows added t))))
    (scattershot-del-loop n-rows added (dec n-threads))
    (prn "Finished deleting at" (System/nanoTime))
    (dm-dosync
     (dotimes [i n-rows]
       (assert (not (scattershot-dm i)))))
    (prn "Finished checking rows deleted at" (System/nanoTime))))
