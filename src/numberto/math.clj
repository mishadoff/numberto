(ns numberto.math
  (:require [numberto.validator :as v])
  (:require [numberto.converters :as c])
  (:require [numberto.seqs :as s]))

(def PI Math/PI)
(def E Math/E)

(declare sum)

(defn count-digits [num]
  (count (c/num->digits num)))

(defn sum-of-digits [num]
  (sum (c/num->digits num)))

(defn reverse-num [num]
  "reverses a number"
  (c/digits->num (reverse (c/num->digits num))))

(defn shift-left [num cnt]
  "shift number digits to the left by specified amount of shifts.
   Number of shifts can be greater than the size of number."
  (let [n (count-digits num)
        [a b] (split-at (mod cnt n) (c/num->digits num))]
    (c/digits->num (concat b a))))

(defn shift-right [num cnt]
  "shift number digits to the right by specified amount of shifts.
   Number of shifts can be greater than the size of number."
  (let [n (count-digits num)
        split-pos (mod (- n cnt) n)]
    (shift-left num split-pos)))

(defn power [x n]
  "x to the nth power"
  (v/validate x :number)
  (v/validate n :integer :non-negative)
  (reduce *' (repeat n x)))

(defn power* [x n]
  "x to the nth power by squaring. O(log n)"
  (v/validate x :number)
  (v/validate n :integer :non-negative)
  (letfn [(pow [x n] 
            (cond (= 0 n) 1
                  (= 1 n) x
                  (even? n) (pow (*' x x) (/ n 2))
                  (odd? n) (*' x (pow (*' x x) (/ (dec n) 2)))))]
    (pow x n)))

(defn square [x]
  "x to the square"
  (power x 2))

(defn sqroot [x]
  "square root of x. always double"
  (v/validate x :number :non-negative)
  (Math/sqrt x))

(defn square? [n]
  "test whether number is exact square or no"
  (= n (square (int (sqroot n)))))

(defn sum [coll]
  "sum all elements in a collection"
  (reduce +' coll))

(defn abs [x]
  "return absolute value of x"
  (v/validate x :number)
  (if (neg? x) (- x) x))

(defn avg [coll]
  "return average of collection of numbers. always double"
  (cond
   (empty? coll) 0
   :else (double (/ (sum coll) (count coll)))))

(defn product [coll]
  "multiplies all elements in collection"
  (reduce *' coll))

(defn gcd [a b]
  "greatest common divisor. Euclidean algorithm"
  (v/validate a :integer)
  (v/validate b :integer)
  (loop [a* (abs a) b* (abs b)]
    (if (zero? b*) a*
        (recur b* (mod a* b*)))))

(defn lcm [a b]
  "least common multiple"
  (let [g (gcd a b)]
    (/ (abs (*' a b)) g)))

(defn factorize [n]
  "factorize number to prime muliplies"
  (v/validate n :integer #(> % 1))
  (loop [x n fact []]
    (if (= 1 x) fact
        (let [d (first (drop-while #(not (zero? (rem x %))) (s/primes)))]
          (recur (/ x d) (conj fact d))))))
