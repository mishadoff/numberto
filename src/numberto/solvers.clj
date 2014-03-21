(ns numberto.solvers
  (:require [numberto.converters :as c])
  (:require [numberto.expression :as e])
  (:require [numberto.math :as m]))

(defn- binary-split [numbers code]
  "Build splits from numbers and binary code indicates gaps
binary-split [1 2 3 4] [:gap :none :gap]) => [1 23 4]"
  (->> (cons nil code)
       (#(interleave % numbers))
       (rest)
       (partition-by #(= :gap %))
       (map #(remove (fn [e] (#{:gap :none} e)) %))
       (remove empty?)
       (map c/digits->num)))

(defn- fill-zeros [n]
  (partial format (str "%0" n "d")))

(defn- splits [numbers]
  (let [n (dec (count numbers))
        limit (m/power* 2 n)]
    (for [i (range limit)]
      (->> (str i)
           (#(c/radix-convert % 10 2))
           (biginteger)
           ((fill-zeros n))
           (seq)
           (map #({\0 :none \1 :gap} %))
           (binary-split numbers)))))

(defn- reverse-lookup [m]
  (into {} (map (fn [[a b]] [b a]) m)))

(defn- valid-permute? [code ops rules]
  (let [reverse-ops (reverse-lookup ops)]
    (loop [[r & rs] rules res true]
      (if (false? res)
        false
        (if r
          (condp = (first r) ;; match tags
            :max
            (let [[_ op val] r k (reverse-ops op)]
              (recur rs (<= (count (filter #(= k %) code)) val)))
            :min
            (let [[_ op val] r k (reverse-ops op)]
              (recur rs (>= (count (filter #(= k %) code)) val)))
            
            
            
            :else (recur rs true)) ;; all rules processed
          true
          )))))

(defn- permute-ops [split ops rules]
  (->> (let [n (count split)]
         (if (= 1 n) [(str (first split))]
             (for [i (->> (count ops)
                          (#(m/power* % (dec n)))
                          (range))]
               (let [code (->> (str i)
                               (#(c/radix-convert % 10 (count ops)))
                               (biginteger)
                               ((fill-zeros (dec n)))
                               (seq))]
                 (if (valid-permute? code ops rules)
                   (->> code
                        (map #(ops %))
                        (cons nil)
                        (#(interleave % split))
                        (rest)
                        (apply str))
                   nil))
               )))
       (remove nil?)))

(defn solve-insert-ops
  ([numbers]
     (solve-insert-ops numbers ["+" "-" "*" "/"] []))
  ([numbers ops rules]
     (let [mapops (->> ops
                       (count)
                       (range 0)
                       (apply str)
                       (seq)
                       (#(zipmap % ops)))] ;; space used to allow 1op
       (map #(vec [(e/eval-infix %) %])
            (mapcat #(permute-ops % mapops rules) (splits numbers))))))

(defn solve-quadratic [a b c]
  "Solve equation: a*x^2 + b*x + c = 0"
  (let [d (- (* b b) (* 4 a c))]
    (cond
     (zero? d) [(/ (- b) (* 2 a))]
     (pos? d) [(/ (- (- b) (Math/sqrt d)) (* 2 a))
               (/ (+ (- b) (Math/sqrt d)) (* 2 a))]
     :else [])))

;; TODO Customizable operations
;; TODO only binaries
;; 10 ops maximum 2 minimum
;; TODO Customizable length
;; TODO Customizable number of uses
;; TODO Parens handler

;; Rules
;; No n in a row
;; No more than
;; Use parens
;;

;; Linear Diphontine: ax + by = 1
;; Pythagorean triples: x^2 + y^2 = z^2
;; Pell's equation: x^2 - n*y^2 = +-1
;; Polynomial solver

(defn solve-polynomial [equation]
  "solve polynomial equation for one variable numerically.
   real and complex roots returned
   equation must be like 10*x^4 +20*x^3 + ... + = 0
"
  ;; Detect max degree
  ;; Apply some numeric method
  )
