(ns numberto.irrational
  (:require [numberto.printers :as p])
  (:require [numberto.seqs :as s]))

;; "Gimme E, Gimme PI, Gimme that which I desire"
;; -- Metallica, Fuel

(defn e [& {:keys [iterations limit] 
            :or {iterations 100 limit 16}}]
  "Expansion of formula 1 + E(1/n!)"
  (->> (range 1 (inc iterations))
       (reductions *')
       (map #(/ 1 %))
       (reduce +')
       (inc)
       (#(p/format-ratio % limit))))

;; Archimedes method depends on sin and rough PI value
;;;;  We can use taylor series for sin approximation 
;; Ramanujan's method depends on sqrt(2) which is also irrational
;; Arctan approximation slow and inaccurate

(defn pi [] nil)

(defn sqrt [num & {:keys [iterations limit] 
                   :or {iterations 100 limit 16}}]
  "Calculate sqroot by continued fraction"
  (->> (s/continued-fraction-sqroot num)
       (take iterations)
       (reverse)
       (reduce #(+ %2 (/ 1 %1)))
       (#(p/format-ratio % limit))))
