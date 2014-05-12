(ns numberto.math-test
  (:require [clojure.test :refer :all]
            [numberto.math :as m]))

(deftest constants-test
  (is (= m/PI Math/PI))
  (is (= m/E Math/E)))

(deftest count-digits-test
  (is (= 3 (m/count-digits 345)))
  (is (= 1 (m/count-digits 0)))
  (is (= 7 (m/count-digits 1254562)))
  (is (thrown? IllegalArgumentException (m/count-digits "123")))
  (is (thrown? IllegalArgumentException (m/count-digits [1]))))

(deftest sum-of-digits-test
  (is (= 12 (m/sum-of-digits 345)))
  (is (= 0 (m/sum-of-digits 0)))
  (is (= 25 (m/sum-of-digits 1254562)))
  (is (thrown? IllegalArgumentException (m/sum-of-digits "123")))
  (is (thrown? IllegalArgumentException (m/sum-of-digits [1]))))

(deftest reverse-num-test
  (is (= 543 (m/reverse-num 345)))
  (is (= 0 (m/reverse-num 0)))
  (is (= 2654521 (m/reverse-num 1254562)))
  (is (thrown? IllegalArgumentException (m/reverse-num "123")))
  (is (thrown? IllegalArgumentException (m/reverse-num [1]))))

(deftest shuffle-num-test
  (is (= 3 (m/count-digits (m/shuffle-num 345))))
  (is (= 7 (m/count-digits (m/shuffle-num 1254562))))
  (let [n 34567235467228362375687352365472364162352467648723658723469182]
    (is (m/permutation? n (m/shuffle-num n)))))

(deftest shift-left-test
  (is (= 123 (m/shift-left 231 2)))
  (is (= 123 (m/shift-left 123 0)))
  (is (= 123 (m/shift-left 123 12)))
  (is (false? (= 123 (m/shift-left 123 11))))
  (is (false? (= 5432 (m/shift-left 5432 2)))))

(deftest shift-right-test
  (is (= 123 (m/shift-right 231 1)))
  (is (= 123 (m/shift-right 123 0)))
  (is (= 123 (m/shift-right 123 12)))
  (is (false? (= 123 (m/shift-right 123 11))))
  (is (false? (= 5432 (m/shift-right 5432 2)))))

(deftest shift-invariant-test
  (doseq [num [12323523] i (range 100)]
    (is (= num (m/shift-left (m/shift-right num i) i)))))

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

;; Lofarithms test

(deftest log-test
  (is (= 3 (bigint (m/log 2 8))))
  (is (= 100 (bigint (m/log 2 (m/power* 2 100)))))
  (is (= 100000 (bigint (m/log 2 (m/power* 2 100000)))))
  (is (thrown? IllegalArgumentException (m/log 1 1000)))
  (is (thrown? IllegalArgumentException (m/log 2 0))))

;; Predicates test

(deftest square?-test
  (is (every? m/square? [0 1 4 9 16 225]))
  (is (every? (comp not m/square?) [11 23 325 123 43 123 87 129 143 12223]))
  (is (false? (m/square? 1025))))

(deftest palindrome?-test
  (is (m/palindrome? 123454321))
  (is (false? (m/palindrome? 1025))))

(deftest permutation?-test
  (is (m/permutation? 1232352 2335221))
  (is (false? (m/permutation? 1232352 2235221)))
  (is (false? (m/permutation? 22111 11121)))
  (is (m/permutation? 9999 9999)))

(deftest div?-test
  (is (m/div? 100500 100500))
  (is (m/div? 123 1))
  (is (m/div? 0 2324))
  (is (false? (m/div? 1 0))))
