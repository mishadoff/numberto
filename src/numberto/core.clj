(ns numberto.core)

;; TODO move to converters
(defn char->digit [char]
  "cast char to digit"
  (- (int char) 48))

(defn digit->char [digit]
  "cast digit to char representation"
  (char (+ digit 48)))

(defn num->digits [num]
  "split a number to the list of digits "
  (map char->digit (seq (str num))))

(defn digits->num [digits]
  "build a number from the list of digits"
  (read-string (apply str digits)))

(defn count-digits [num]
  "return amount of digits for number"
  (count (num->digits num)))

(defn palindrome? [num]
  (let [coll (num->digits num)]
    (= (reverse coll) coll)))

(defn digit? [digit]
  "Test whether number is one-digit [0-9]"
  (<= 0 digit 9))

(defn reverse-num [num]
  "reverses a number"
  (digits->num (reverse (num->digits num))))

(defn shift-left [num cnt]
  "shift number digits to the left by specified amount of shifts.
   Number of shifts can be greater than the size of number."
  (let [n (count-digits num)
        [a b] (split-at (mod cnt n) (num->digits num))]
    (digits->num (concat b a))))

(defn shift-right [num cnt]
  "shift number digits to the right by specified amount of shifts.
   Number of shifts can be greater than the size of number."
  (let [n (count-digits num)
        split-pos (mod (- n cnt) n)]
    (shift-left num split-pos)))

(defn permutation? [num1 num2]
  "test whether two numbers are permutations of each other' digits"
  (let [f (comp frequencies num->digits)]
    (= (f num1) (f num2))))

;; TODO pandigital
