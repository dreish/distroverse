

(use :reload-all 'durable-maps)

; Harmless to run this if it has already been run:
(dm-init!)

(dm-create-map! "test1"
                {:cols {:mykey ["VARCHAR(32)" :str]
                        :myval ["TEXT" :obj]}
                 :key :mykey})

(def test1-map (dm-get-map "test1"))

(dm-dosync (dm-insert test1-map {:mykey "foo" :myval 10}))

(dm-dosync (test1-map "foo"))

