(ns numberto.math)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;; Utils package for mathematic functions and constants
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Constants

(def PI Math/PI)
(def E Math/E)

;; Functions

(defn power [x n]
  "x to the nth power. n must be an integer"
  (reduce *' (repeat n x)))

(defn square [x]
  "x to the square. similar to (* x x)"
  (power x 2))

(defn sqroot [x]
  "square root of x. always double"
  (Math/sqrt x))

(defn square? [n]
  "test whether number is exact square or no"
  (= n (square (int (sqroot n)))))

(defn sum [coll]
  "sum all elements in collection of numbers"
  (reduce +' coll))

(defn abs [x]
  "return absolute value of x"
  (if (neg? x) (- x) x))

(defn avg [coll]
  "return average of collection of numbers. always double"
  (double (/ (sum coll) (count coll))))

(defn product [coll]
  "multiplies all elements in collection"
  (reduce *' coll))

(defn factorial [n]
  "return factorial of number. n must be a non-negative integer"
  (reduce *' (range 1 (inc n))))

(defn gcd [a b]
  "greatest common divisor. Euclidean algorithm"
  (if (zero? b) a
      (recur b (mod a b))))

(defn lcm [a b]
  "least common multiple"
  (/ (abs (*' a b)) (gcd a b)))