(ns numberto.predicates-test
  (:use [clojure.test])
  (:require [numberto.predicates :as p]))

(deftest digit?-test
  (is (every? p/digit? [0 1 2 3 4 5 6 7 8 9]))
  (is (every? (comp not p/digit?) [11 23 324 123 43 123 87 129 143 12223]))
  (is (false? (p/digit? 12)))
  (is (false? (p/digit? "Hello")))
  (is (false? (p/digit? [1]))))

(deftest square?-test
  (is (every? p/square? [0 1 4 9 16 225]))
  (is (every? (comp not p/square?) [11 23 325 123 43 123 87 129 143 12223]))
  (is (false? (p/square? 1025))))

(deftest palindrome?-test
  (is (p/palindrome? 123454321))
  (is (false? (p/palindrome? 1025))))

(deftest permutation?-test
  (is (p/permutation? 1232352 2335221))
  (is (false? (p/permutation? 1232352 2235221)))
  (is (false? (p/permutation? 22111 11121)))
  (is (p/permutation? 9999 9999)))

(run-tests)














