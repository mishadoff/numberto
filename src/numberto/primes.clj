(ns numberto.primes)

(defn primes []
  "Lazy sequence of prime numbers"
  (letfn [(next-prime [p ps]
            (cond (some #(zero? (mod p %)) (take-while #(<= (* % %) p) ps))
                  (recur (+ p 2) ps)
                  :else (cons p (lazy-seq (next-prime (+ p 2) (conj ps p))))))]
    (cons 2 (lazy-seq (next-prime 3 [])))))

;; TODO [REFACTOR] move to predicates
(defn prime? [p]
  "check whether number is prime. Complexity O(sqrt(p))"
  (and (> p 1)
       (not (some #(zero? (mod p %))
                  (take-while #(<= (* % %) p) (range 2 p))))))

(defn factorize [n]
  "factorize number to prime muliplies"
  (loop [x n fact []]
    (if (= 1 x) fact
        (let [d (first (drop-while #(not (zero? (rem x %))) (primes)))]
          (recur (/ x d) (conj fact d))))))

;; TODO [TEST]
(defn totient [n]
  "Euler's totient function. BigInt."
  (reduce * n (map #(- 1 (/ 1 %)) (distinct (factorize n)))))

;; TODO perfect numbers
;; TODO amicable numbers
;; TODO abundant
;; TODO carmichael function
