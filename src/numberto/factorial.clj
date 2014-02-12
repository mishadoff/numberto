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
  "Improved version of factorial by factorization. 
   Works better for large numbers."
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

(def ^:private ROUND java.math.RoundingMode/HALF_EVEN)

(defn !-apx [n]
  (letfn [(scale [d s] (.setScale d s ROUND))
          (pow [x n]
             (cond (= 0 n) 1
                   (= 1 n) x
                   (even? n) (pow (scale (*' x x) 10) (/ n 2))
                   (odd? n) (*' x (pow (scale (*' x x) 10) (/ (dec n) 2)))))]
    "Stirling approximation of n!"
    (scale
     (* (scale (bigdec (Math/sqrt (* 2 Math/PI n))) 10)
        (pow (scale (bigdec (+ (/ n Math/E)
                        (/ 1 (* 12 Math/E n)))) 10) n)) 0)))

;; !-apx 7500 ms (m/power* accumulate scale 20)
;; !-apx 2700 ms (m/power* accumulate scale 10)
;; !-apx 560 ms (pow without accumulate scale 10)
;; ! 750 ms
;; !! 650 ms
