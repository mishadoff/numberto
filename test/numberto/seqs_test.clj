(ns numberto.seqs-test
  (:use [clojure.test])
  (:require [numberto.seqs :as s]))

(deftest naturals-test
  (is (= [1 2 3 4 5] (take 5 s/naturals))))

(deftest squares-test
  (is (= [1 4 9 16 25] (take 5 s/squares))))

(deftest powers-of-two-test
  (is (= [1 2 4 8 16] (take 5 s/powers-of-two))))

(deftest triangles-test
  (is (= [1 3 6 10 15] (take 5 s/triangles))))

(deftest primes-test
  (is (= 2 (first (s/primes))))
  (is (= 13 (nth (s/primes) 5)))
  (is (= 104743 (nth (s/primes) 10000))))

(run-tests)

