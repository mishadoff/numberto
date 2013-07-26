(ns numberto.predicates-test
  (:use [clojure.test])
  (:require [numberto.predicates :as p]))

(deftest digit?-test
  (is (every? p/digit? [0 1 2 3 4 5 6 7 8 9]))
  (is (false? (p/digit? 12)))
  (is (false? (p/digit? "Hello")))
  (is (false? (p/digit? [1]))))

(run-tests)

