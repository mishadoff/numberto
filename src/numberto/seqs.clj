(ns numberto.seqs)

;; All infinite sequences are lazy
;; If numbers extremely large, they will be promoted to BigInteger
;;
;; !!! Do not forget to cut results when evaluating lazy seqs

;; Natural numbers [1 2 3 ...]
(def naturals (iterate (partial +' 1) 1))

;; Square numbers [1 4 9 16 ...]
(def squares (map #(* % %) naturals))

;; Powers of two [1 2 4 8 16 ...]
(def powers-of-two (iterate (partial *' 2) 1))

;; Triangle numbers
(def triangles (reductions + naturals))

;; Prime numbers
(defn primes []
  "Lazy sequence of prime numbers"
  (letfn [(next-prime [p ps]
            (cond (some #(zero? (mod p %)) (take-while #(<= (* % %) p) ps))
                  (recur (+ p 2) ps)
                  :else (cons p (lazy-seq (next-prime (+ p 2) (conj ps p))))))]
    (cons 2 (lazy-seq (next-prime 3 [])))))
