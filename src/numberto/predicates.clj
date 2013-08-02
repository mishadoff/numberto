(ns numberto.predicates
  (:require [numberto.converters :as conv])
  (:require [numberto.core :as core])
  (:require [numberto.math :as math]))

(defn digit? [d]
  (conv/digit? d))

(defn palindrome? [num]
  (= num (core/reverse-num num)))

(defn square? [n]
  (math/square? n))

(defn permutation? [num1 num2]
  "test whether two numbers are permutations of each other' digits"
  (let [f (comp frequencies conv/num->digits)]
    (= (f num1) (f num2))))
