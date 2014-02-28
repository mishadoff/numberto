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

(defn fill-zeros [n]
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

(defn- permute-ops [split]
  (let [n (count split)]
    (if (= 1 n) [(str split)]
        (for [i (range (m/power* 4 (dec n)))]
          (->> (str i)
               (#(c/radix-convert % 10 4))
               (biginteger)
               ((fill-zeros (dec n)))
               (seq)
               (map #({\0 '+ \1 '- \2 '* \3 '/} %))
               (cons nil)
               (#(interleave % split))
               (rest)
               (apply str))))))

(defn insert-ops-solver [numbers]
  (map #(vec [(e/eval-infix %) %]) (permute-ops numbers)))


(defn solve-quadratic [a b c]
  "Solve equation: a*x^2 + b*x + c = 0"
  (let [d (- (* b b) (* 4 a c))]
    (cond
     (zero? d) [(/ (- b) (* 2 a))]
     (pos? d) [(/ (- (- b) (Math/sqrt d)) (* 2 a))
               (/ (+ (- b) (Math/sqrt d)) (* 2 a))]
     :else (throw (IllegalArgumentException. "No real solution")))))

;; TODO Customizable operations
;; TODO Parens handler

;; Linear Diphontine: ax + by = 1
;; Pythagorean triples: x^2 + y^2 = z^2
;; Pell's equation: x^2 - n*y^2 = +-1
