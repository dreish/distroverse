(ns distroverse.core-test
  (:use [distroverse core util protocol] :reload-all)
  (:use [clojure.test])
  (:use [midje.sweet]))

;;; Number goes in, number comes out.
(doseq [n [0 1 2 3 4 7 8 15 16 20 30 40 50 80 127 128 129 255 256 257
           511 512 513 514 1022 1023 1024 1025 1026 8191 8192 8193
           100000 100000000 100000000000000 1000000000000000000000000]]
  (fact (bytes-to-ulong (ulong-to-bytes n))
        => (return-pair n ())))

(doseq [s ["" "hi" "\0" "Who's done some FORTRAN?"]]
  (fact (bytes-to-string (string-to-bytes s))
        => (return-pair s ())))

(doseq [n [0 1 2 -1 -2 0.5 -0.5 0.25 -0.25 0.125 -0.125
           0.0001 -0.0001 1e10 -1e10]]
  (fact (bytes-to-float (float-to-bytes (float n)))
        => (return-pair (float n) ())))

(doseq [n [0 1 2 -1 -2 0.5 -0.5 0.25 -0.25 0.125 -0.125
           0.0001 -0.0001 1e-100 -1e-100 1e10 -1e10 1e100 -1e100]]
  (fact (bytes-to-double (double-to-bytes (double n)))
        => (return-pair (double n) ())))

(defn server-running?
  ([]
     false))

(defn server-tests
  ([]
     (fact )))

(if (server-running?)
  (server-tests)
  (println "WARNING: skipping server tests, server is not running"))

