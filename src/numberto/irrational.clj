(ns numberto.irrational
  (:require [numberto.printers :as p])
  (:require [numberto.math :as m])
  (:require [numberto.seqs :as s]))

;; "Gimme E, Gimme PI, Gimme that which I desire"
;; -- Metallica, Fuel

(defn e [& {:keys [iterations limit] 
            :or {iterations 100 limit 16}}]
  "Expansion of formula 1 + E(1/n!)"
  (->> (range 1 (inc iterations))
       (reductions *')
       (map #(/ 1 %))
       (reduce +' 1)
       (#(p/format-ratio % limit))))

(defn pi [& {:keys [iterations limit] 
             :or {iterations 100 limit 16}}]
  "Calculate PI by Rabinowitz algorithm"
  (->> (range 1 (inc iterations))
       (map #(/ (m/product (take %1 s/naturals))
                (m/product (take %1 (filter odd? s/naturals)))))
       (reduce +')
       (* 2)
       (dec)
       (dec)
       (#(p/format-ratio % limit))))
       
(defn sqrt [num & {:keys [iterations limit] 
                   :or {iterations 100 limit 16}}]
  "Calculate sqroot by continued fraction"
  (->> (s/continued-fraction-sqroot num)
       (take iterations)
       (reverse)
       (reduce #(+ %2 (/ 1 %1)))
       (#(p/format-ratio % limit))))
