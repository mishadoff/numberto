(ns numberto.math)

;; Utils package for mathematic methods and constants

(def PI Math/PI)

(defn power [x n]
  (reduce *' (repeat n x)))

(def square (power % 2))

(defn factorial [n]
  (reduce *' (range 1 (inc n))))

;; TODO Euler's Totient Function