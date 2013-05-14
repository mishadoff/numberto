(ns numberto.seqs)

;; All infinite sequences are lazy
;; If numbers extremely large, they will be promoted to BigInteger
;;
;; !!! Do not forget to cut results when evaluating lazy seqs

;; Natural numbers [1 2 3 ...]
(def natural (iterate (partial +' 1) 1))

;; Square numbers [1 4 9 16 ...]
(def squares (map #(* % %) natural))

;; Powers of two [1 2 4 8 16 ...]
(def powers-of-two (iterate (partial *' 2) 1))