(ns numberto.primes-test
  (:use [clojure.test])
  (:require [numberto.primes :as p]))

(deftest primes-test
  (is (= 2 (first (p/primes))))
  (is (= 13 (nth (p/primes) 5)))
  (is (= 104743 (nth (p/primes) 10000))))

(deftest factorize-test
  (is (= [2 5] (p/factorize 10)))
  (is (= [104743] (p/factorize 104743)))
  (is (= [2 2 2 2 2 2 2 2] (p/factorize 256)))
  (is (thrown? IllegalArgumentException (p/factorize 0)))
  (is (thrown? IllegalArgumentException (p/factorize 1)))
  (is (thrown? IllegalArgumentException (p/factorize 1.0)))
  (is (thrown? IllegalArgumentException (p/factorize -120)))
  (is (thrown? IllegalArgumentException (p/factorize "a"))))



(run-tests)

