(ns numberto.farey
  (:require [numberto.math :as m]))

;; Farey sequence. Lazy. Finite.
;;
;; http://en.wikipedia.org/wiki/Farey_sequence

(defn farey [n]
  (letfn [(next-farey [r1 r2]
            (let [[a b] r1 [c d] r2
                  k (quot (+ n b) d)
                  next-term [(- (* k c) a) (- (* k d) b)]]
              (if (<= (first next-term) n)
                (cons next-term (lazy-seq (next-farey r2 next-term))))))]
    (let [a 0 b 1 c 1 d n]
      (concat [[a b] [c d]] (lazy-seq (next-farey [a b] [c d]))))))

(defn farey-size [n]
  "TODO"
  )

(defn farey-size-approximate [n]
  "Good approximation for Farey Sequence"
  (/ (* 3 n n) (m/sqr m/PI)))