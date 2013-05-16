(ns numberto.fractions
  (:require [numberto.math :as m])
  (:require [numberto.seqs :as seqs]))

(defn convergent [seq]
  "Number of convergents, sequence"
  (letfn [(partial-sum [fseq]
            (reduce #(+ (/ 1 %1) %2) (reverse fseq)))] ;; TODO improve performance
    (map #(partial-sum (take % seq)) seqs/naturals)))

(defn continued-fraction-sqroot-seq [n]
  "TODO document"
  (let [a0 (int (m/sqroot n))]
    (letfn [(next-frac [a m d]
              (let [m1 (- (* a d) m)
                    d1 (/ (- n (m/square m1)) d)
                    a1 (quot (+ a0 m1) d1)]
                (cons a1 (lazy-seq (next-frac a1 m1 d1)))))]
      (if (= (m/square a0) n) (list a0)
          (cons a0 (lazy-seq (next-frac a0 0 1)))))))