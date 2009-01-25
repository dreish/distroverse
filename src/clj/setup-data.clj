

(use :reload-all 'durable-maps)

; Harmless to run this if it has already been run:
(dm-init!)

(dm-create-map! "test1"
                {:cols {:mykey ["VARCHAR(32)" :str]
                        :myval ["TEXT" :obj]}
                 :key :mykey})

(def my-test1 (dm-get-map "test1"))

(dm-dosync (dm-insert my-test1 {:mykey "foo" :myval 10}))

(dm-dosync (my-test1 "foo"))

