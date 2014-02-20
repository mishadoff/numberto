(ns numberto.solvers
  (:require [numberto.converters :as c])
  (:require [numberto.math :as m]))

(def ^:private ops [+ - * /])

(defn- binary-split [numbers code]
  "Build splits from numbers and binary code indicates gaps
binary-split [1 2 3 4] [:gap :none :gap]) => [1 23 4]"
  (->> (cons nil code)
       (#(interleave % numbers))
       (rest)
       (partition-by #(= :gap %))
       (map #(remove (fn [e] (#{:gap :none} e)) %))
       (remove empty?)
       (map c/digits->num)))

(defn- all-splits [numbers]
  (let [n (dec (count numbers))
        fill-zeros (partial format (str "%0" n "d"))
        limit (m/power* 2 n)]
    (for [i (range limit)]
      (->> (str i)
           (#(c/radix-convert % 10 2))
           (biginteger)
           (fill-zeros)
           (seq)
           (map #({\0 :none \1 :gap} %))
           (binary-split numbers)))))
