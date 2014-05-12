(ns numberto.irrational
  (:require [numberto.printers :as p])
  (:require [numberto.math :as m])
  (:require [numberto.validator :as v])
  (:require [numberto.seqs :as s]))

;; "Gimme E, Gimme PI, Gimme that which I desire"
;; -- Metallica, Fuel

(defn e 
  "Expansion of formula 1 + E(1/n!)"
  [& {:keys [iterations limit] 
      :or {iterations 100 limit 16}}]
  (->> (range 1 (inc iterations))
       (reductions *')
       (map #(/ 1 %))
       (reduce +' 1)
       (#(p/format-ratio % limit))))

(defn pi 
  "Calculate PI by Rabinowitz algorithm"
  [& {:keys [iterations limit] 
      :or {iterations 100 limit 16}}]
  (->> (range 1 (inc iterations))
       (map #(/ (m/product (take %1 s/naturals))
                (m/product (take %1 (filter odd? s/naturals)))))
       (reduce +')
       (* 2)
       (dec)
       (dec)
       (#(p/format-ratio % limit))))

(defn sqrt 
  "Calculate sqroot by continued fraction"
  [num & {:keys [iterations limit] 
          :or {iterations 100 limit 16}}]
  (v/validate num :integer :positive)
  (->> (s/continued-fraction-sqroot num)
       (take iterations)
       (reverse)
       (reduce #(+ %2 (/ 1 %1)))
       (#(p/format-ratio % limit))))
