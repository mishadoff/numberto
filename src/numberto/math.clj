(ns numberto.math)

;; Utils package for mathematic methods and constants
;; TODO document

(def PI Math/PI)
(def E Math/E)

(defn power [x n]
  "n must be an integer"
  (reduce *' (repeat n x)))

(def square #(power % 2))
(def sqroot #(Math/sqrt %))

(defn square? [n]
  (= n (square (int (sqroot n)))))

(defn sum [coll]
  (reduce +' coll))

(defn abs [n]
  (if (neg? n) (- n) n))

(defn avg [coll]
  (double (/ (sum coll) (count coll))))

(defn product [coll]
  (reduce *' coll))

(defn factorial [n]
  (reduce *' (range 1 (inc n))))

;; TODO gcd
;; TODO lcm
;; TODO Euler's Totient Function
