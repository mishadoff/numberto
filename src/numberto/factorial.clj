(ns numberto.factorial
  (:require [numberto.primes :as p])
  (:require [numberto.math :as m]))

(defn ! [n]
  "Standard factorial version"
  (reduce *' (range 1 (inc n))))

(defn- find-power [n k]
  "Return the power of factorization prime number k for number n!"
  (loop [total n sum 0]
    (let [i (int (/ total k))]
      (if (zero? i) sum
          (recur i (+ sum i))))))

(defn !! [n]
  "Improved version of factorial by factorization"
  (reduce *'
          (map #(m/power* % (find-power n %))
               (take-while #(<= % n) (p/primes)))))
