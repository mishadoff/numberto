(ns numberto.factorial
  (:require [numberto.primes :as p]))

(defn ! [n]
  "Standard factorial version"
  (reduce *' (range 1 (inc n))))

(defn- find-power [n k]
  "Return the power of factorization prime number k for number n!"
  (loop [total n sum 0]
    (let [i (int (/ total k))]
      (if (zero? i) sum
          (recur i (+ sum i))))))

;; TODO move to math
(defn power* [x n]
  "x to the nth power by squaring. O(log n)"
  (cond 
   (= 0 n) 1
   (= 1 n) x
   (even? n) (power* (*' x x) (/ n 2))
   (odd? n) (*' x (power* (*' x x) (/ (dec n) 2)))))


(defn !! [n]
  "Improved version of factorial by factorization"
  (reduce *' 
          (map #(apply power* [% (find-power n %)])
               (take-while #(< % n) (p/primes)))))
