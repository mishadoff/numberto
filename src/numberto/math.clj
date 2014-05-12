(ns numberto.math
  (:require [numberto.validator :as v])
  (:require [numberto.converters :as c]))

(def PI Math/PI)
(def E Math/E)

(defn sum
  "sum all elements in a collection"
  [coll]
  (reduce +' coll))

(defn count-digits 
  "find count of digits in a number"
  [num]
  (count (c/num->digits num)))

(defn sum-of-digits 
  "find sum of number digits"
  [num]
  (sum (c/num->digits num)))

(defn sum-of-digits-recur
  "recursive sum of digits"
  [num]
  (if (c/digit? num) num (recur (sum-of-digits num))))

(defn reverse-num 
  "reverses a number"
  [num]
  (c/digits->num (reverse (c/num->digits num))))

(defn shuffle-num
  "shuffle a number"
  [num]
  (c/digits->num (shuffle (c/num->digits num))))

(defn shift-left
  "shift number digits to the left by specified amount of shifts.
   Number of shifts can be greater than the size of number."
  [num cnt]
  (let [n (count-digits num)
        [a b] (split-at (mod cnt n) (c/num->digits num))]
    (c/digits->num (concat b a))))

(defn shift-right
  "shift number digits to the right by specified amount of shifts.
   Number of shifts can be greater than the size of number."
  [num cnt]
  (let [n (count-digits num)
        split-pos (mod (- n cnt) n)]
    (shift-left num split-pos)))

(defn power
  "x to the nth power"
  [x n]
  (v/validate x :number)
  (v/validate n :integer :non-negative)
  (reduce *' (repeat n x)))

;; What about bigdec (.pow)?
(defn power*
  "x to the nth power by squaring. O(log n)"
  [x n]
  (v/validate x :number)
  (v/validate n :integer :non-negative)
  (letfn [(pow [x n acc] 
            (cond (= 0 n) acc
                  (= 1 n) (*' x acc)
                  (even? n) (recur (*' x x) (/ n 2) acc)
                  (odd? n) (recur (*' x x) (/ (dec n) 2) (*' x acc))))]
    (pow x n 1)))

(defn square
  "x to the square"
  [x]
  (power x 2))

(defn sqroot
  "square root of x. always double"
  [x]
  (v/validate x :number :non-negative)
  (Math/sqrt x))

(defn abs
  "return absolute value of x"
  [x]
  (v/validate x :number)
  (if (neg? x) (- x) x))

(defn avg
  "return average of collection of numbers. always double"
  [coll]
  (cond (empty? coll) 0
        :else (double (/ (sum coll) (count coll)))))

(defn product
  "multiplies all elements in collection"
  [coll]
  (reduce *' coll))

(defn gcd
  "greatest common divisor. Euclidean algorithm"
  [a b]
  (v/validate a :integer)
  (v/validate b :integer)
  (loop [a* (abs a) b* (abs b)]
    (if (zero? b*) a*
        (recur b* (mod a* b*)))))

(defn lcm
  "least common multiple"
  [a b]
  (let [g (gcd a b)]
    (/ (abs (*' a b)) g)))

;; Logarithms

(def ^:private LOG_LIMIT (reduce *' (repeat 1000 2)))

(defn log
  "log_base(n) for large numbers. Result may be inaccurate"
  [base n]
  (v/validate n :number :positive)
  (v/validate base :number #(> % 1))
  (letfn [(log* [base n]
            (if (<= n LOG_LIMIT) 
              (/ (Math/log n)
                 (Math/log base))
              (+ (log* base (/ n LOG_LIMIT))
                 (log* base LOG_LIMIT))))]
    (log* base n)))

(def log2 (partial log 2))

;; Predicates

(defn square?
  "test whether number is exact square or no"
  [n]
  (= n (square (int (sqroot n)))))

(defn palindrome?
  "test whether number is a palindrome"
  [num]
  (= num (reverse-num num)))

(defn permutation? 
  "test whether two numbers are permutations of each other' digits"
  [num1 num2]
  (let [f (comp frequencies c/num->digits)]
    (= (f num1) (f num2))))

(defn div? 
  "return true if a divisible by b (b|a)"
  [a b]
  (and (not (zero? b))
       (zero? (mod a b))))
