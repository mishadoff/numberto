(ns numberto.converters
  (:require [numberto.predicates :as p]))

(defn char->digit [char]
  "cast char to digit"
  (let [n (- (int char) 48)]
    (cond 
     (p/digit? n) n
     :else (throw (IllegalArgumentException. "char must be a convertable number")))))

(defn digit->char [digit]
  "cast digit to char representation"
  (cond
   (p/digit? digit) (char (+ digit 48))
   :else (throw (IllegalArgumentException. "digit must be a number [0-9]"))))

(defn num->digits [num]
  "split an integer number to the list of digits"
  (cond 
   (integer? num) (map char->digit (seq (str num)))
   :else (throw (IllegalArgumentException. "num must be an integer"))))

(defn digits->num [digits]
  "construct a number from list of digits"
  (cond
   (every? p/digit? digits) (bigint (apply str digits))
   :else (throw (IllegalArgumentException. "digits must contain only numbers [0-9]"))))

;; TODO binary, hex, oct
