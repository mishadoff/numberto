(ns numberto.converters
  (:require [numberto.validator :as v])
  (:require [clojure.string :as s]))

;; Number converters

(defn digit? [digit]
  "Test whether number is one-digit [0-9]"
  (and (integer? digit) (<= 0 digit 9)))

(defn char->digit [c]
  "Cast char to digit"
  (let [n (- (int c) 48)]
    (cond 
     (digit? n) n
     :else (throw (IllegalArgumentException. "char must be a convertable number")))))

(defn digit->char [d]
  "Cast digit to char"
  (cond
   (digit? d) (char (+ d 48))
   :else (throw (IllegalArgumentException. "digit must be a number [0-9]"))))

(defn num->digits [n]
  "Split an integer number to the list of digits"
  (v/validate n :integer)
  (map char->digit (seq (str n))))

(defn digits->num [ds]
  "Construct a number from list of digits"
  (cond
   (every? digit? ds) (bigint (apply str ds))
   :else (throw (IllegalArgumentException. "digits must contain only numbers [0-9]"))))

;; Roman numbers

(def to-roman-map
  (sorted-map-by > 1 "I" 4 "IV" 5 "V" 9 "IX" 10 "X"
                 40 "XL" 50 "L" 90 "XC" 100 "C"
                 400 "CD" 500 "D" 900 "CM" 1000 "M"))

(defn number->roman [num]
  "Convert arabic number to Roman representation"
  (v/validate num :integer :positive)
  (letfn [(closest [n] 
            (->> (keys to-roman-map)
                 (drop-while #(> % n))
                 first))
          (roman-seq [n acc]
            (if (zero? n) acc
                (let [c (closest n)]
                  (recur (- n c) (conj acc c)))))]
    (apply str (map to-roman-map (roman-seq num [])))))

(defn roman->number [s]
  "Convert roman number to arabic."
  (let [upper-cased (s/upper-case s)]
    (v/validate upper-cased :string #(re-matches #"[MDCLXVI]+" %))
    (->> (reverse upper-cased)
         (map (zipmap "MDCLXVI" [1000 500 100 50 10 5 1]))
         (partition-by identity)
         (map (partial reduce +))
         (reduce #(if (< %1 %2) (+ %1 %2) (- %1 %2))))))
