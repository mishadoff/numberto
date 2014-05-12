(ns numberto.primes-test
  (:require [clojure.test :refer :all]
            [numberto.primes :as p]))

(deftest primes-test
  (is (= 2 (first (p/primes))))
  (is (= 13 (nth (p/primes) 5)))
  (is (= 104743 (nth (p/primes) 10000))))

(deftest prime?-test
  (is (p/prime? 2))
  (is (every? p/prime? (take 100 (p/primes))))
  (is (p/prime? 104743))
  (is (false? (p/prime? 1)))
  (is (false? (p/prime? -100)))
  (is (false? (p/prime? 666666))))

(deftest factorize-test
  (is (= [2 5] (p/factorize 10)))
  (is (= [104743] (p/factorize 104743)))
  (is (= [2 2 2 2 2 2 2 2] (p/factorize 256)))
  (is (thrown? IllegalArgumentException (p/factorize 0)))
  (is (thrown? IllegalArgumentException (p/factorize 1)))
  (is (thrown? IllegalArgumentException (p/factorize 1.0)))
  (is (thrown? IllegalArgumentException (p/factorize -120)))
  (is (thrown? IllegalArgumentException (p/factorize "a"))))

(deftest totient-test
  (is (= 12 (p/totient 36)))
  (is (= 96 (p/totient 97)))
  (doseq [i (take 100 (p/primes))]
    (is (= (dec i) (p/totient i))))
  (is (thrown? IllegalArgumentException (p/totient 0))))

(deftest sum-of-proper-divisors-test
  (is (= 8 (p/sum-of-proper-divisors 10)))
  (is (= 284 (p/sum-of-proper-divisors 220)))
  (is (= 220 (p/sum-of-proper-divisors 284)))
  (is (thrown? IllegalArgumentException (p/sum-of-proper-divisors -10)))
  (is (thrown? IllegalArgumentException (p/sum-of-proper-divisors 1.0)))
  (is (thrown? IllegalArgumentException (p/sum-of-proper-divisors "a")))
  (is (thrown? IllegalArgumentException (p/sum-of-proper-divisors 0)))
  (is (thrown? IllegalArgumentException (p/sum-of-proper-divisors 1))))

(deftest amicable?-test
  (is (p/amicable? 220 284))
  (is (false? (p/amicable? 6 6)))
  (is (false? (p/amicable? 10 20))))

(deftest perfect?-test
  (is (p/perfect? 6))
  (is (false? (p/perfect? 7)))
  (is (false? (p/perfect? 8))))
