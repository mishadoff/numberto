(ns numberto.core)

(defn char->digit [c]
  (- (int c) 48))

(defn num->digits [n]
  (map char->digit (seq (str n))))

(defn palindrome? [n]
  (let [coll (num->digits n)]
    (= (reverse coll) coll)))

;; TODO reverse num
;; TODO permutations?
;; TODO pandigital
;; TODO shift number (right, left)