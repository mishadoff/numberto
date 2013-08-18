(ns numberto.generators)

(defn rand-digit []
  "Return random digit [0-9]"
  (rand-int 10))

(defn rand-number [n]
  "Return random number with size of n."
  (->> (repeatedly (dec n) rand-digit)
       (cons (inc (rand-int 9)))
       (apply str)
       bigint))
