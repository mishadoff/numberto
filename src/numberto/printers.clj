(ns numberto.printers
  (:require [numberto.converters :as c])
  (:require [numberto.validator :as v])
  (:require [numberto.math :as m]))

(def default-properties 
  {:s 5 :e 5 :cnt true})

(defn format-num
  "Print long number in short format"
  ([num] (format-num num default-properties))
  ([num props]
     (v/validate num :integer :non-negative)
     (let [{s :s e :e cnt :cnt} (merge default-properties props)
           num-seq (c/num->digits num) n (count num-seq)]
       (if (< (+ s e) n)
         (str (apply str (take s num-seq))
              "..."
              (if cnt (str "[" (- n s e) "]...") "") 
              (apply str (drop (- n e) num-seq)))
         (str num)))))

(defn format-ratio [ratio limit]
  "Print ratio number with limit accuracy"
  ;; TODO validate ratio
  ;; TODO tests
  (let [numbers
        (loop [n (numerator ratio) d (denominator ratio) it 0 res []]
          (cond (= it limit) res
                (= 0 n) res
                (< n d) (recur (* 10 n) d (inc it) (conj res 0))
                (>= n d) (recur (* 10 (mod n d)) d (inc it) (conj res (quot n d)))))]
    (apply str (concat [(first numbers) "."] (rest numbers))))) 
        

(def ^:private number-names 
  {1 "one" 2 "two" 3 "three" 4 "four" 5 "five" 6 "six" 7 "seven"
   8 "eight" 9 "nine" 10 "ten" 11 "eleven" 12 "twelve" 13 "thirteen"
   14 "fourteen" 15 "fifteen" 16 "sixteen" 17 "seventeen" 18 "eighteen"
   19 "nineteen" 20 "twenty" 30 "thirty" 40 "forty" 50 "fifty"
   60 "sixty" 70 "seventy" 80 "eighty" 90 "ninety" 100 "hundred"})

;; number means *3 zeros
(def ^:private big-names
  {1 "thousand" 2 "million" 3 "billion" 4 "trillion" 5 "quadrillion"
   6 "quintillion" 7 "sextillion" 8 "septillion" 9 "octillion"
   10 "ninillion" 11 "decillion" 12 "undecillion" 13 "duodecillion"
   14 "tredecillion" 15 "quattuordecillion" 16 "quindecillion"
   17 "sexdecillion" 18 "septendecillion" 19 "octodecillion" 
   20 "novemdecillion" 21 "vigintillion" 22 "unvigintillion"
   23 "duovigintillion" 24 "trevigintillion" 25 "quattuorvigintillion"
   26 "quinvigintillion" 27 "sexvigintillion" 28 "septenvigintillion"
   29 "octovigintillion" 30 "novemvigintillion" 31 "trigintillion"
   32 "untrigintillion" 33 "duotrigintillion"})

(defn number-name [num]
  "Convert number to english word representation. 
   10^102 max number supported."
  (v/validate num :integer #(< (m/count-digits %) 102))
  (letfn [(closest [num]
            (->> (keys number-names)
                 sort
                 reverse
                 (drop-while #(> % num))
                 first))
          (less100 [num hundred] ;; merge 100 + 1000
            (loop [n num s hundred]
              (if (zero? n) (apply str (interpose " " s))
                  (let [close (closest n)]
                    (recur (- n close) (conj s (get number-names close)))))))
          (less1000 [num]
            (let [k (quot num 100)]
              (if (zero? k) (less100 num [])
                  (less100 (mod num 100) [(get number-names k)
                                          (get number-names 100)]))))
          (bignum-indexed [i num]
            (let [s (less1000 num)]
              (if (pos? i) 
                (if-not (empty? s) (str s " " (get big-names i)))
                (if-not (empty? s) s))))]
    (->> num
         c/num->digits
         reverse
         (partition 3 3 [0 0 0])
         (map #(c/digits->num (reverse %)))
         (map-indexed bignum-indexed)
         reverse
         (remove nil?)
         (interpose " ")
         (apply str))))
  
