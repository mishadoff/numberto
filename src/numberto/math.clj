(ns numberto.math)

;; TODO [ENHANCEMENT] return needed number of digits
(def PI Math/PI)
(def E Math/E)

(defn power [x n]
  "x to the nth power"
  (cond 
   (not (number? x)) (throw (IllegalArgumentException. "x must be a number"))
   (or (not (integer? n)) (neg? n)) (throw (IllegalArgumentException. "n must be a non-negative integer"))
   :else (reduce *' (repeat n x))))

(defn power* [x n]
  "x to the nth power by squaring. O(log n)"
  (cond 
   (not (number? x)) (throw (IllegalArgumentException. "x must be a number"))
   (or (not (integer? n)) (neg? n)) (throw (IllegalArgumentException. "n must be a non-negative integer"))
   :else (letfn [(pow [x n] 
                   (cond (= 0 n) 1
                         (= 1 n) x
                         (even? n) (pow (*' x x) (/ n 2))
                         (odd? n) (*' x (pow (*' x x) (/ (dec n) 2)))))]
           (pow x n))))

(defn square [x]
  "x to the square"
  (power x 2))

(defn sqroot [x]
  "square root of x. always double"
  (cond
   (or (not (number? x)) (neg? x)) (throw (IllegalArgumentException. "x must be a non-negative number"))
   :else (Math/sqrt x)))

;; TODO [ENHANCEMENT] replace Math/sqrt as it returns double
;; TODO [IMPL] implement sqroot-int

;; TODO [TEST] cover with tests
;; TODO [IMPL] extend for bignumbers

;; TOOD [REFACTOR] move to predicate?
(defn square? [n]
  "test whether number is exact square or no"
  (= n (square (int (sqroot n)))))

(defn sum [coll]
  "sum all elements in a collection"
  (reduce +' coll))

(defn abs [x]
  "return absolute value of x"
  (cond 
   (not (number? x)) (throw (IllegalArgumentException. "x must be a number"))
   (neg? x) (- x) 
   :else x))

(defn avg [coll]
  "return average of collection of numbers. always double"
  (cond
   (empty? coll) 0
   :else (double (/ (sum coll) (count coll)))))

(defn product [coll]
  "multiplies all elements in collection"
  (reduce *' coll))

;; TODO [TEST] cover
;; TODO [IMPL] Error handling
(defn gcd [a b]
  "greatest common divisor. Euclidean algorithm"
  (if (zero? b) a
      (recur b (mod a b))))

(defn lcm [a b]
  "least common multiple"
  (/ (abs (*' a b)) (gcd a b)))
