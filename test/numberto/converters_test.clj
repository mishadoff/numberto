(ns numberto.converters-test
  (:require [clojure.test :refer :all]
            [numberto.converters :as c]))

(deftest digit?-test
  (is (every? c/digit? [0 1 2 3 4 5 6 7 8 9]))
  (is (every? (comp not c/digit?) [11 23 324 123 43 123 87 129 143 12223]))
  (is (false? (c/digit? 12)))
  (is (false? (c/digit? "Hello")))
  (is (false? (c/digit? [1]))))

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
  (is (= 1223445 (c/digits->num [1 2 2 3 4 4 5])))
  (is (thrown? IllegalArgumentException (c/digits->num [1 2 "3"])))
  (is (thrown? IllegalArgumentException (c/digits->num ["99N"]))))

(deftest number->roman-test
  (is (= "CXXIII" (c/number->roman 123))) 
  (is (= "IX" (c/number->roman 9))) 
  (is (= "MCMLXXXIX" (c/number->roman 1989)))
  (is (thrown? IllegalArgumentException (c/number->roman 0)))
  (is (thrown? IllegalArgumentException (c/number->roman -100)))
  (is (thrown? IllegalArgumentException (c/number->roman 1.0))))

(deftest roman->number-test
  (is (= 123 (c/roman->number "CXXIII")))
  (is (= 9 (c/roman->number "IX"))) 
  (is (= 1989 (c/roman->number "MCMLXXXIX")))
  (is (= 3 (c/roman->number "iii")))
  (is (= 37 (c/roman->number "xxxVIi")))
  (is (thrown? IllegalArgumentException (c/roman->number "0")))
  (is (thrown? IllegalArgumentException (c/roman->number "WOW"))))

(deftest roman-invariant-test
  (doseq [i (range 1 2000)]
    (is (= i (c/roman->number (c/number->roman i))))))

(deftest radix-convert-test
  (is (= "1001" (c/radix-convert "9" 10 2)))
  (is (= "166" (c/radix-convert "166" 17 17)))
  (is (= "255" (c/radix-convert "FF" 16 10)))
  (is (thrown? IllegalArgumentException (c/radix-convert "hello" 15 10)))
  (is (thrown? IllegalArgumentException (c/radix-convert "1020" 1 10)))
  (is (thrown? IllegalArgumentException (c/radix-convert "1020" 10 39))))

(deftest radix-convert-invariant-test
  (doseq [radix (range 2 37)]
    (is (= "1010" (c/radix-convert "1010" radix radix)))
    (is (= "101010101010" (c/radix-convert "101010101010" radix radix)))
    ))
