(ns numberto.primes)

;; Prime numbers
(defn primes []
  (letfn [(next-prime [p ps]
            (cond (some #(zero? (mod p %)) (take-while #(<= (* % %) p) ps))
                  (recur (+ p 2) ps)
                  :else (cons p (lazy-seq (next-prime (+ p 2) (conj ps p))))))]
    (cons 2 (lazy-seq (next-prime 3 [])))))

(defn prime? [p]
  (and (> p 1)
       (not (some #(zero? (mod p %))
                  (take-while #(<= (* % %) p) (range 2 p))))))

(defn factorize [n]
  (loop [x n fact []]
    (if (= 1 x) fact
        (let [d (first (drop-while #(not (zero? (rem x %))) (primes)))]
          (recur (/ x d) (conj fact d))))))

(defn totient [n]
  (reduce * n (map #(- 1 (/ 1 %)) (factorize n))))
