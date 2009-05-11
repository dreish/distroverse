
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

(use :reload-all 'bigkey-dm)

(dm-startup! :sql "dm" "dm" "nZe3a5dL")

; Harmless to run this if it has already been run:
(dm-init!)

(dm-create-map! "test1"
                {:cols {:mykey ["VARCHAR(32)" :str]
                        :myval ["TEXT" :obj]}
                 :key :mykey})

(def my-test1 (dm-get-map "test1"))

(dm-dosync (dm-insert my-test1 {:mykey "foo", :myval 10}))

(prn (dm-dosync (my-test1 "foo")))

(dm-dosync (dm-delete my-test1 "foo"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(bk-startup!)

(bk-init!)

(bk-create-map! "bktest1"
                {:abstract-keycol :key
                  ; md5 is only 32 chars; oops
                 :key-munger `(md5-munger 40)
                 :key-type ["VARCHAR(40)" :str]
                 :val-type ["MEDIUMTEXT" :obj]})

(def bk-test1 (dm-dosync (bk-get-map "bktest1")))

(dm-dosync (bk-insert bk-test1 {:key [1 1 2 3 5 8]
                                :any-key "xyz"}))

(dm-dosync (bk-test1 [1 1 2 3 5 8]))

(dm-dosync (bk-update bk-test1 [1 1 2 3 5 8] assoc :up :up))

(dm-dosync (bk-delete bk-test1 [1 1 2 3 5 8]))


(bk-create-map! "bktest3"
                {:abstract-keycol :key
                  ; md5 is only 32 chars; oops
                 :key-munger `(md5-munger 1)
                 :key-type ["CHAR(1)" :str]
                 :val-type ["MEDIUMTEXT" :obj]})

(def bk-test3 (dm-dosync (bk-get-map "bktest3")))

(dm-dosync (bk-insert bk-test3 {:key [1 1 2 3 5 8]
                                :any-key "xyz"}))

(dm-dosync (bk-test3 [1 1 2 3 5 8]))

(dm-dosync (bk-update bk-test3 [1 1 2 3 5 8] assoc :up :down))

(dm-dosync (bk-insert bk-test3 {:key [1 1 2 3 5 8 4]
                                :any-key "xyz again"}))

(dm-dosync (bk-test3 [1 1 2 3 5 8 4]))

(dm-dosync (bk-delete bk-test3 [1 1 2 3 5 8 4]))
