(ns numberto.converters
  (:require [numberto.predicates :as p]))

(defn char->digit [c]
  "cast char to digit"
  (let [n (- (int c) 48)]
    (cond 
     (p/digit? n) n
     :else (throw (IllegalArgumentException. "char must be a convertable number")))))

(defn digit->char [d]
  "cast digit to char representation"
  (cond
   (p/digit? d) (char (+ d 48))
   :else (throw (IllegalArgumentException. "digit must be a number [0-9]"))))

(defn num->digits [n]
  "split an integer number to the list of digits"
  (cond 
   (integer? n) (map char->digit (seq (str n)))
   :else (throw (IllegalArgumentException. "num must be an integer"))))

(defn digits->num [ds]
  "construct a number from list of digits"
  (cond
   (every? p/digit? ds) (bigint (apply str ds))
   :else (throw (IllegalArgumentException. "digits must contain only numbers [0-9]"))))

;; TODO binary, hex, oct
