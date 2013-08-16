(ns numberto.factorial
  (:require [numberto.primes :as p])
  (:require [numberto.validator :as v])
  (:require [numberto.math :as m]))

(defn ! [n]
  "Standard factorial function. Returns 1 * 2 * 3 * ... * n"
  (v/validate n :integer :non-negative)
  (loop [cur n acc 1]
    (if (zero? cur) acc
        (recur (dec cur) (*' cur acc)))))

(defn !! [n]
  "Improved version of factorial by factorization. Works better for large numbers."
  (v/validate n :integer :non-negative)
  (letfn [(find-power [n k]
            (loop [total n sum 0]
              (let [i (int (/ total k))]
                (if (zero? i) sum
                    (recur i (+ sum i))))))]
    (loop [[h & t] 
           (map #(m/power* % (find-power n %))
                (take-while #(<= % n) (p/primes)))
           acc 1]
      (if h (recur t (*' h acc)) acc))))

;; DOES NOT WORK FOR LARGE NUMBERS
(defn stirling-approximation [n]
  "Stirling approximation of factorial. O(log(n))"
  (v/validate n :number)
  (* (m/power* (bigdec (/ n m/E)) n)
     (bigdec (m/sqroot (* 2 m/PI n)))))
