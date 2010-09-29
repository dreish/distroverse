;; Copyright (C) 2010 Dan Reish

;; The use and distribution terms for this software are covered by the
;; Eclipse Public License 1.0
;; (http://opensource.org/licenses/eclipse-1.0.php) which can be found
;; in the file COPYING at the root of this distribution.  By using
;; this software in any fashion, you are agreeing to be bound by the
;; terms of this license.  You must not remove this notice, or any
;; other, from this software.

;;; Contrasted with test_spec.clj, this is a dumping ground for tests
;;; of non-protocol code, or of parts of the protocol that are not yet
;;; permanently set.  There are no particular rules for what may go
;;; here or how it may be changed.

(ns distroverse.test-working
  (:use [distroverse protocol] :reload-all)
  (:use [clojure.test])
  (:use [midje.sweet]))

