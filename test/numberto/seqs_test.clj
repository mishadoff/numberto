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

(deftest fibonacci-test
  (is (= [1 1 2 3 5 8 13 21 34 55] (take 10 s/fibonacci))))

(deftest continued-fraction-sqroot-test
  (is (= [0] (s/continued-fraction-sqroot 0)))
  (is (= [1] (s/continued-fraction-sqroot 1)))
  (is (= [1 2 2 2 2] (take 5 (s/continued-fraction-sqroot 2))))
  (is (= [17 1 4 8 1] (take 5 (s/continued-fraction-sqroot 317))))
  (is (thrown? IllegalArgumentException (s/continued-fraction-sqroot "a"))))

(deftest farey-test
  (is (= [[0 1] [1 5] [1 4] [1 3] [2 5] [1 2] [3 5]
          [2 3] [3 4] [4 5] [1 1]] (s/farey 5)))
  (is (= [[3 197] [5 328] [2 131] [5 327] [3 196] [4 261] 
          [5 326] [1 65] [5 324] [4 259]] (take 10 (drop 500 (s/farey 333)))))
  (is (thrown? IllegalArgumentException (s/farey 0)))
  (is (thrown? IllegalArgumentException (s/farey -10)))
  (is (thrown? IllegalArgumentException (s/farey "123"))))