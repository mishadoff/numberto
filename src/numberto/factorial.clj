(ns numberto.factorial
  (:require [numberto.primes :as p])
  (:require [numberto.math :as m]))

(defn ! [n]
  "Standard factorial version"
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
  "Improved version of factorial by factorization"
  (loop [[h & t] 
         (map #(m/power* % (find-power n %))
              (take-while #(<= % n) (p/primes)))
         acc 1]
    (if h (recur t (*' h acc)) acc)))

(defn next-factorial [n value]
  "Input: n, and value=n!
   Output: n+1 and (n+1)!"
  [(inc n) (*' value (inc n))])

;; (defn !* [n]
;;   "Returns stirling approximation of factorial"
;;   (*' (m/power* (bigdec (/ n m/E)) n)
;;       (m/sqroot (*' 2 m/PI n))))
