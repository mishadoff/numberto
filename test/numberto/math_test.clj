(ns numberto.math-test
  (:use [clojure.test])
  (:require [numberto.math :as m]))

(deftest constants-test
  (is (= m/PI Math/PI))
  (is (= m/E Math/E)))

(deftest power-test
  (doseq [pow [m/power m/power*]]
    (is (= 16 (pow 2 4)))
    (is (= 16 (pow 4 2)))
    (is (= 1 (pow 100 0)))
    (is (= 0 (pow 0 100)))
    (is (= 1 (pow 0 0)))
    (is (thrown? IllegalArgumentException (pow "1" 2)))
    (is (thrown? IllegalArgumentException (pow 1 1.5)))
    (is (thrown? IllegalArgumentException (pow "300" "10")))
    (is (thrown? IllegalArgumentException (pow 4 -1)))))

(deftest square-test
  (is (= 16 (m/square 4)))
  (is (= 225 (m/square 15)))
  (is (thrown? IllegalArgumentException (m/square "10")))
  (is (thrown? IllegalArgumentException (m/square [1]))))

(deftest sqroot-test 
  (is (= 2.0 (m/sqroot 4)))
  (is (= 15.0 (m/sqroot 225.0)))
  (is (thrown? IllegalArgumentException (m/sqroot "10")))
  (is (thrown? IllegalArgumentException (m/sqroot -10))))

(deftest sum-test
  (is (= 6 (m/sum [1 2 3])))
  (is (= 0 (m/sum []))))

(deftest abs-test
  (is (= 100 (m/abs 100)))
  (is (= 100.0 (m/abs -100.0)))
  (is (= 0 (m/abs 0)))
  (is (thrown? IllegalArgumentException (m/abs "1"))))

(deftest avg-test
  (is (= 3.0 (m/avg [1 2 3 4 5])))
  (is (= 0 (m/avg []))))

(deftest product-test
  (is (= 120 (m/product [1 2 3 4 5])))
  (is (= 1 (m/product []))))

(deftest gcd-test
  (is (= 1 (m/gcd 3 5)))
  (is (= 10 (m/gcd 3528375238957920 38523856236590)))
  (is (= 2 (m/gcd 6 4)))
  (is (= 3 (m/gcd 6 3)))
  (is (= 6 (m/gcd 6 0)))
  (is (= 0 (m/gcd 0 0)))
  (is (= 6 (m/gcd -54 -24))) ;; Not implemeted for non-negatives
  (is (= 6 (m/gcd -54 24)))
  (is (= 6 (m/gcd 54 -24)))
  (is (thrown? IllegalArgumentException (m/gcd 1 "a")))
  (is (thrown? IllegalArgumentException (m/gcd "a" 1)))
  (is (thrown? IllegalArgumentException (m/gcd "a" "b"))))

(deftest lcm-test
  (is (= 42 (m/lcm 21 6)))
  (is (= 42 (m/lcm -21 6)))
  (is (= 42 (m/lcm 21 -6)))
  (is (= 42 (m/lcm -21 -6))))

(run-tests)

