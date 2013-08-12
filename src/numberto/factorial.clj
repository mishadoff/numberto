(ns numberto.factorial
  (:require [numberto.seqs :as s])
  (:require [numberto.validator :as v])
  (:require [numberto.math :as m]))

(defn ! [n]
  "Standard factorial version"
  (v/validate n :integer :non-negative)
  (loop [cur n acc 1]
    (if (zero? cur) acc
        (recur (dec cur) (*' cur acc)))))

(defn- find-power [n k]
  "Return the power of factorization prime number k for number n!"
  (loop [total n sum 0]
    (let [i (int (/ total k))]
      (if (zero? i) sum
          (recur i (+ sum i))))))

(defn !! [n]
  (v/validate n :integer :non-negative)
  "Improved version of factorial by factorization"
  (loop [[h & t] 
         (map #(m/power* % (find-power n %))
              (take-while #(<= % n) (s/primes)))
         acc 1]
    (if h (recur t (*' h acc)) acc)))
