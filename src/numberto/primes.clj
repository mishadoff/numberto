(ns numberto.primes
  (:require [numberto.validator :as v])
  (:require [numberto.math :as m]))

(defn primes []
  "Lazy sequence of prime numbers"
  (letfn [(next-prime [p ps]
            (cond (some #(zero? (mod p %)) (take-while #(<= (* % %) p) ps))
                  (recur (+ p 2) ps)
                  :else (cons p (lazy-seq (next-prime (+ p 2) (conj ps p))))))]
    (cons 2 (lazy-seq (next-prime 3 [])))))

;; More prime numbers testing
(defn prime? [p]
  "test whether number is prime. Complexity O(sqrt(p))"
  (and (> p 1)
       (not (some #(zero? (mod p %))
                  (take-while #(<= (* % %) p) (range 2 p))))))

(defn factorize [n]
  "factorize number to prime muliplies"
  (v/validate n :integer #(> % 1))
  (loop [x n fact []]
    (if (= 1 x) fact
        (let [d (first (drop-while #(not (zero? (rem x %))) (primes)))]
          (recur (/ x d) (conj fact d))))))

(defn totient [n]
  "Euler's totient function"
  (reduce * n (map #(- 1 (/ 1 %)) (distinct (factorize n)))))

(defn sum-of-proper-divisors [n]
  "Sum of proper divisors. d(10) = (+ 1 2 5)"
  (v/validate n :integer #(> % 1))
  (let [base (filter #(zero? (mod n %)) (range 2 (Math/sqrt n)))]
    (reduce + 1 (concat (map #(/ n %) base) base))))

(defn amicable? [a b]
  (and (not (= a b))
       (= a (sum-of-proper-divisors b))
       (= b (sum-of-proper-divisors a))))

(defn perfect? [a]
  (= a (sum-of-proper-divisors a)))
