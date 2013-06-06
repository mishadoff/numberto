(ns numberto.fibonacci)

(def fibonacci
  "Generate fibonacci sequence. Lazy"
  (map second (iterate (fn [[a b]] [b (+' a b)]) [0 1])))

;; TODO additional fib methods
