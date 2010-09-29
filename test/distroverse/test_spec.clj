;;; This file is in the public domain.

;;; This set of tests constitutes the standard for DVTP, the protocol
;;; spoken between envoy and client.  It may be used for clean-room
;;; reimplementations of the protocol code where the EPL is
;;; undesireable.

;;; No test may be removed from this script or substantially changed
;;; without incrementing the first nonzero version number of
;;; Distroverse.  (E.g., 0.5.5 -> 0.6.0, or 1.3.5 -> 2.0.0.)  Consider
;;; that fact carefully before adding a new test to this script; once
;;; it is added, that part of the protocol is etched in stone until
;;; breaking changes are allowed with the next major revision.
;;; Consider also that it is hoped Distroverse will never require a
;;; version 2.0.0.

;;; Most tests should go in test_working.clj.

(ns distroverse.test-spec
  (:use [distroverse protocol] :reload-all)
  (:use [clojure.test])
  (:use [midje.sweet]))

;;; Number goes in, number comes out.
(doseq [n [0 1 2 3 4 7 8 15 16 20 30 40 50 80 127 128 129 255 256 257
           511 512 513 514 1022 1023 1024 1025 1026 8191 8192 8193
           100000 100000000 100000000000000 1000000000000000000000000]]
  (fact (bytes-to-ulong (ulong-to-bytes n))
        => (return-pair n ())))

(doseq [ [bs ul]  [ [[0] 0]     [[1] 1]  [[7] 7]
                     [[50] 50]   [[-127 8] 1025]
                     [[-128 -62 -41 47] 100000000] ] ]
  (fact (bytes-to-ulong (map byte bs))
        => (return-pair ul ()))
  (fact (ulong-to-bytes ul)
        => (seq bs)))

(fact (bytes-to-ulong (map byte [-10 -20 -30 40 50 60]))
      => (return-pair 85505654 '(50 60)))

(doseq [s ["" "hi" "\0" "Who's done some FORTRAN?"]]
  (fact (bytes-to-string (string-to-bytes s))
        => (return-pair s ())))

(doseq [ [bs s] [ [[0        ] ""  ]
                  [[2 104 105] "hi"]
                  [[1 0      ] "\0"]
                  [[20 48 49 50 51 52 53 54 55 56 57 48
                    97 98 99 100 101 102 103 104 105]
                   "01234567890abcdefghi"]
                  [(list* -56 1 (repeat 200 49))
                   (apply str (repeat 200 "1"))] ] ]
  (fact (bytes-to-string (map byte bs))
        => (return-pair s ()))
  (fact (string-to-bytes s)
        => (seq bs)))

(fact (bytes-to-string (map byte [5 65 97 97 97 97 97 97 97 97 97]))
      => (return-pair "Aaaaa" '(97 97 97 97 97)))

(doseq [n [0 1 2 -1 -2 0.5 -0.5 0.25 -0.25 0.125 -0.125
           0.0001 -0.0001 1e10 -1e10]]
  (fact (bytes-to-float (float-to-bytes (float n)))
        => (return-pair (float n) ())))

(doseq [ [bs f] [ [[0 0 0 0       ]      0]
                  [[0 0 -128 63   ]      1]
                  [[0 0 -128 -65  ]     -1]
                  [[-85 -86 -86 62]    1/3]
                  [[40 107 110 78 ]  1.0e9]
                  [[95 112 -119 48] 1.0e-9] ]]
  (fact (bytes-to-float (map byte bs))
        => (return-pair (float f) ()))
  (fact (float-to-bytes (float f))
        => (seq bs)))

(fact (bytes-to-float (map byte [1 23 45 67 8 9 10]))
      => (return-pair (float 173.08986)
                      '(8 9 10)))

(doseq [n [0 1 2 -1 -2 0.5 -0.5 0.25 -0.25 0.125 -0.125
           0.0001 -0.0001 1e-100 -1e-100 1e10 -1e10 1e100 -1e100]]
  (fact (bytes-to-double (double-to-bytes (double n)))
        => (return-pair (double n) ())))

(doseq [ [bs d] [ [[0 0 0 0 0 0 0 0              ]       0]
                  [[0 0 0 0 0 0 -16 63           ]       1]
                  [[0 0 0 0 0 0 -16 -65          ]      -1]
                  [[85 85 85 85 85 85 -43 63     ]     1/3]
                  [[46 -97 -121 -94 -82 66 125 84]  1.0e99]
                  [[62 -61 -40 78 125 127 97 43  ] 1.0e-99] ] ]
  (fact (bytes-to-double (map byte bs))
        => (return-pair (double d) ()))
  (fact (double-to-bytes (double d))
        => (seq bs)))

(fact (bytes-to-double (map byte [105 87 20 -117 10
                                  -65 5 64 0 -63 -5 62]))
      => (return-pair 2.718281828459045 '(0 -63 -5 62)))

(fact (bytes-to-messages (map byte [0 1 0 1]))
      => '({:type :ulong, :value 1}
           {:type :ulong, :value 1}))

(fact (bytes-to-messages (map byte [0 -19 -65 -111 4 1 4 69 108 108 111]))
      => '({:type :ulong, :value 8675309}
           {:type :string, :value "Ello"}))

;;; Not sure how this is even supposed to work:

;; (defn server-running?
;;   ([]
;;      false))

;; (defn server-tests
;;   ([]
;;      (fact )))

;; (if (server-running?)
;;   (server-tests)
;;   (println "WARNING: skipping server tests, server is not running"))

