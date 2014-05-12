(ns numberto.generators)

(defn rand-digit 
  "Return random digit [0-9]"
  []
  (rand-int 10))

(defn rand-number 
  "Return random number with size of n."
  [n]
  (->> (repeatedly (dec n) rand-digit)
       (cons (inc (rand-int 9)))
       (apply str)
       bigint))

(defn rand-bigint 
  "Return random bigint below num, the same as (rand-int n)"
  [num]
  (let [bits (.bitLength (bigint num))]
    (loop [] 
      (let [val (BigInteger. bits (java.util.Random.))]
        (if (< val num) val (recur))))))
