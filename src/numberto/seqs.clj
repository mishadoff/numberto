(ns numberto.seqs
  (:require [numberto.math :as m])
  (:require [numberto.validator :as v]))

;; Natural numbers [1 2 3 ...]
(def naturals (iterate (partial +' 1) 1))

;; Square numbers [1 4 9 16 ...]
(def squares (map #(* % %) naturals))

;; Powers of two [1 2 4 8 16 ...]
(def powers-of-two (iterate (partial *' 2) 1))

;; Triangle numbers [1 3 6 10 15 ...]
(def triangles (reductions + naturals))

;; Prime numbers
(defn primes []
  "Lazy sequence of prime numbers"
  (letfn [(next-prime [p ps]
            (cond (some #(zero? (mod p %)) (take-while #(<= (* % %) p) ps))
                  (recur (+ p 2) ps)
                  :else (cons p (lazy-seq (next-prime (+ p 2) (conj ps p))))))]
    (cons 2 (lazy-seq (next-prime 3 [])))))

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
