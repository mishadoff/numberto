(ns numberto.seqs
  (:require [numberto.math :as m])
  (:require [numberto.validator :as v])
  (:require [numberto.converters :as c]))

;; Natural numbers [1 2 3 ...]
(def naturals (iterate (partial +' 1) 1))

;; Square numbers [1 4 9 16 ...]
(def squares (map #(* % %) naturals))

;; Powers-of
(defn powers-of [n] (iterate (partial *' n) 1))

;; Powers of two [1 2 4 8 16 ...]
(def powers-of-two (iterate (partial *' 2) 1))

;; Triangle numbers [1 3 6 10 15 ...]
(def triangles (reductions + naturals))

;; Pentagonal numbers [1, 5, 12, 22 ...]
(def pentagonals (map #(/ (- (* 3 (m/square %)) %) 2) naturals))

;; Hexagonal number (property: every other triangle number)
(def hexagonals (map #(/ (* 2 % (dec (* 2 %))) 2) naturals))

;; Fibonacci [1 1 2 3 5 8 13 ... ]
(def fibonacci
  "Generate fibonacci sequence"
  (map second (iterate (fn [[a b]] [b (+' a b)]) [0 1])))

;;; Fractions
(defn continued-fraction-sqroot [n]
  "Sequence of continued fractions"
  (v/validate n :integer :non-negative)
  (let [a0 (int (m/sqroot n))]
    (letfn [(next-frac [a m d]
              (let [m1 (- (* a d) m)
                    d1 (/ (- n (m/square m1)) d)
                    a1 (quot (+ a0 m1) d1)]
                (cons a1 (lazy-seq (next-frac a1 m1 d1)))))]
      (if (= (m/square a0) n) (list a0)
          (cons a0 (lazy-seq (next-frac a0 0 1)))))))

;; Farey sequence. Lazy. Finite.
;; http://en.wikipedia.org/wiki/Farey_sequence

(defn farey [n]
  (v/validate n :integer :positive)
  (letfn [(next-farey [r1 r2]
            (let [[a b] r1 [c d] r2
                  k (quot (+ n b) d)
                  next-term [(- (* k c) a) (- (* k d) b)]]
              (if (<= (first next-term) n)
                (cons next-term (lazy-seq (next-farey r2 next-term))))))]
    (let [a 0 b 1 c 1 d n]
      (concat [[a b] [c d]] (lazy-seq (next-farey [a b] [c d]))))))

(defn palindromes []
  "Returns sorted lazy sequence of palindromic numbers."
  (let [s (atom 1)
        pal (fn [n f]
              (let [ds (c/num->digits n)]
                (c/digits->num (concat ds (f (reverse ds))))))
        next-pal (fn [[n v]]
                   (if (> (m/count-digits (inc v)) @s)
                     (do
                       (swap! s inc)
                       (let [newn (m/product (repeat (quot (dec @s) 2) 10))]
                         [newn (pal newn (if (even? @s) identity rest))]))
                     [(inc n) (pal (inc n) (if (even? @s) identity rest))]))]
    (map second (iterate next-pal [0N 0N]))))
