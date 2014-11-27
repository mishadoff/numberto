(ns numberto.converters
  (:require [numberto.validator :as v])
  (:require [clojure.string :as s]))

;; Number converters
(defn digit?
  "Test whether number is one-digit [0-9]"
  [digit]
  (and (integer? digit) (<= 0 digit 9)))

(defn char->digit
  "Cast char to digit"
  [c]
  (let [n (- (int c) 48)]
    (cond 
     (digit? n) n
     :else (throw (IllegalArgumentException. "char must be a convertable number")))))

(defn digit->char
  "Cast digit to char"
  [d]
  (cond
   (digit? d) (char (+ d 48))
   :else (throw (IllegalArgumentException. "digit must be a number [0-9]"))))

(defn num->digits
  "Split an integer number to the list of digits"
  [n]
  (v/validate n :integer)
  (map char->digit (seq (str n))))

(defn digits->num
  "Construct a number from list of digits"
  [ds]
  (cond (every? digit? ds) (bigint (clojure.string/join ds))
        :else (throw (IllegalArgumentException. "digits must contain only numbers [0-9]"))))

;; Roman numbers

(def to-roman-map
  (sorted-map-by > 1 "I" 4 "IV" 5 "V" 9 "IX" 10 "X"
                 40 "XL" 50 "L" 90 "XC" 100 "C"
                 400 "CD" 500 "D" 900 "CM" 1000 "M"))

(defn number->roman
  "Convert arabic number to Roman representation"
  [num]
  (v/validate num :integer :positive)
  (letfn [(closest [n] 
            (->> (keys to-roman-map)
                 (drop-while #(> % n))
                 first))
          (roman-seq [n acc]
            (if (zero? n) acc
                (let [c (closest n)]
                  (recur (- n c) (conj acc c)))))]
    (clojure.string/join (map to-roman-map (roman-seq num [])))))

(defn roman->number
  "Convert roman number to arabic."
  [s]
  (let [upper-cased (s/upper-case s)]
    (v/validate upper-cased :string #(re-matches #"[MDCLXVI]+" %))
    (->> (reverse upper-cased)
         (map (zipmap "MDCLXVI" [1000 500 100 50 10 5 1]))
         (partition-by identity)
         (map (partial reduce +))
         (reduce #(if (< %1 %2) (+ %1 %2) (- %1 %2))))))

(defn radix-convert
  "Convert integer in string format in base of from-radix to to-radix."
  [num from-radix to-radix]
  (v/validate from-radix :integer [#(<= 2 % 36) "must be in range [2..36]"])
  (v/validate to-radix :integer [#(<= 2 % 36) "must be in range [2..36]"])
  (v/validate num :string 
              [#(try (do (BigInteger. % from-radix) true)
                     (catch NumberFormatException e false))
               "must be in format of base [from-radix]"])
  ;; validate num is valid in from
  (-> num
      (BigInteger. from-radix)
      (.toString to-radix)))
