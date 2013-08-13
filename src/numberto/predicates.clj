(ns numberto.predicates
  (:require [numberto.converters :as c])
  (:require [numberto.math :as m]))

(defn digit? [d]
  (c/digit? d))

(defn palindrome? [num]
  "test whether number is a palindrome"
  (= num (m/reverse-num num)))

(defn square? [n]
  "test whether number is square"
  (m/square? n))

(defn permutation? [num1 num2]
  "test whether two numbers are permutations of each other' digits"
  (let [f (comp frequencies c/num->digits)]
    (= (f num1) (f num2))))

(defn prime? [p]
  "test whether number is prime. Complexity O(sqrt(p))"
  (and (> p 1)
       (not (some #(zero? (mod p %))
                  (take-while #(<= (* % %) p) (range 2 p))))))
