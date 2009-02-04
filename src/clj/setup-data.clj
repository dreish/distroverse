

(use :reload-all 'durable-maps)

(use :reload-all 'bigkey-dm)

; Harmless to run this if it has already been run:
(dm-init!)

(dm-create-map! "test1"
                {:cols {:mykey ["VARCHAR(32)" :str]
                        :myval ["TEXT" :obj]}
                 :key :mykey})

(def my-test1 (dm-get-map "test1"))

(dm-dosync (dm-insert my-test1 {:mykey "foo", :myval 10}))

(prn (dm-dosync (my-test1 "foo")))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

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

(dm-dosync (bk-update bk-test1 [1 1 2 3 5 8] assoc :up :date))




(bk-create-map! "bktest2"
                {:abstract-keycol :key
                  ; md5 is only 32 chars; oops
                 :key-munger `#(md5-munger 1)
                 :key-type ["CHAR(1)" :str]
                 :val-type ["MEDIUMTEXT" :obj]})

(def bk-test2 (dm-dosync (bk-get-map "bktest2")))

(dm-dosync (bk-insert bk-test2 {:key [1 1 2 3 5 8]
                                :any-key "xyz"}))

(dm-dosync (bk-test2 [1 1 2 3 5 8]))

(dm-dosync (bk-update bk-test2 [1 1 2 3 5 8] assoc :up :date))

