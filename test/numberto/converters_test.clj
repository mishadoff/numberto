(ns numberto.converters-test
  (:use [clojure.test])
  (:require [numberto.converters :as c]))

(deftest char->digit-test
  (is (= [0 1 2 3 4 5 6 7 8 9]
         (map c/char->digit "0123456789")))
  (is (thrown? IllegalArgumentException (c/char->digit \a))))

(deftest digit->char-test
  (is (= "0123456789"
         (apply str (map c/digit->char [0 1 2 3 4 5 6 7 8 9]))))
  (is (thrown? IllegalArgumentException (c/digit->char 12))))

(deftest num->digits-test
  (is (= [1 2 3] (c/num->digits 123)))
  (is (= [9 9 9] (c/num->digits 999N)))
  (is (= [1 2 2 3 4 4 5] (c/num->digits 1223445)))
  (is (thrown? IllegalArgumentException (c/num->digits "123"))))

(deftest digits->num-test
  (is (= 123 (c/digits->num [1 2 3])))
  (is (= 999N (c/digits->num [9 9N 9])))
  (is (= 1223445 ) (c/digits->num [1 2 2 3 4 4 5]))
  (is (thrown? IllegalArgumentException (c/digits->num [1 2 "3"])))
  (is (thrown? IllegalArgumentException (c/digits->num ["99N"]))))
