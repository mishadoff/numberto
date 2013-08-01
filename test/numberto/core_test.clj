(ns numberto.core-test
  (:use [clojure.test])
  (:require [numberto.core :as c]))

(deftest count-digits-test
  (is (= 3 (c/count-digits 345)))
  (is (= 1 (c/count-digits 0)))
  (is (= 7 (c/count-digits 1254562)))
  (is (thrown? IllegalArgumentException (c/count-digits "123")))
  (is (thrown? IllegalArgumentException (c/count-digits [1]))))

(deftest sum-of-digits-test
  (is (= 12 (c/sum-of-digits 345)))
  (is (= 0 (c/sum-of-digits 0)))
  (is (= 25 (c/sum-of-digits 1254562)))
  (is (thrown? IllegalArgumentException (c/sum-of-digits "123")))
  (is (thrown? IllegalArgumentException (c/sum-of-digits [1]))))

(deftest reverse-num-test
  (is (= 543 (c/reverse-num 345)))
  (is (= 0 (c/reverse-num 0)))
  (is (= 2654521 (c/reverse-num 1254562)))
  (is (thrown? IllegalArgumentException (c/reverse-num "123")))
  (is (thrown? IllegalArgumentException (c/reverse-num [1]))))

(deftest shift-left-test
  (is (= 123 (c/shift-left 231 2)))
  (is (= 123 (c/shift-left 123 0)))
  (is (= 123 (c/shift-left 123 12)))
  (is (false? (= 123 (c/shift-left 123 11))))
  (is (false? (= 5432 (c/shift-left 5432 2)))))

(deftest shift-right-test
  (is (= 123 (c/shift-right 231 1)))
  (is (= 123 (c/shift-right 123 0)))
  (is (= 123 (c/shift-right 123 12)))
  (is (false? (= 123 (c/shift-right 123 11))))
  (is (false? (= 5432 (c/shift-right 5432 2)))))

(deftest shift-invariant-test
  (doseq [num [12323523] i (range 100)]
    (is (= num (c/shift-left (c/shift-right num i) i)))))

(run-tests)  
  













