(ns numberto.converters
  (:require [numberto.validator :as v]))

(defn digit? [digit]
  "Test whether number is one-digit [0-9]"
  (and (integer? digit) (<= 0 digit 9)))

(defn char->digit [c]
  "cast char to digit"
  (let [n (- (int c) 48)]
    (cond 
     (digit? n) n
     :else (throw (IllegalArgumentException. "char must be a convertable number")))))

(defn digit->char [d]
  "cast digit to char representation"
  (cond
   (digit? d) (char (+ d 48))
   :else (throw (IllegalArgumentException. "digit must be a number [0-9]"))))

(defn num->digits [n]
  "split an integer number to the list of digits"
  (v/validate n :integer)
  (map char->digit (seq (str n))))

(defn digits->num [ds]
  "construct a number from list of digits"
  (cond
   (every? digit? ds) (bigint (apply str ds))
   :else (throw (IllegalArgumentException. "digits must contain only numbers [0-9]"))))
