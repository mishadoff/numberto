(ns numberto.core
  (:require [numberto.math :as m])
  (:require [numberto.converters :as c]))

(defn count-digits [num]
  (count (c/num->digits num)))

(defn sum-of-digits [num]
  (m/sum (c/num->digits num)))

(defn palindrome? [num]
  (let [coll (c/num->digits num)]
    (= (reverse coll) coll)))

(defn digit? [digit]
  "Test whether number is one-digit [0-9]"
  (<= 0 digit 9))

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

(defn permutation? [num1 num2]
  "test whether two numbers are permutations of each other' digits"
  (let [f (comp frequencies c/num->digits)]
    (= (f num1) (f num2))))

;; TODO pandigital
