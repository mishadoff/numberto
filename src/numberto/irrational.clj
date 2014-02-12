(ns numberto.irrational
  (:require [numberto.printers :as p]))

;; "Gimme E, Gimme PI, Gimme that which I desire"
;; -- Metallica, Fuel

(defn e [& {:keys [iterations limit] 
                  :or {iterations 100 limit 16}}]
  (->> (range 1 (inc iterations))
       (reductions *')
       (map #(/ 1 %))
       (reduce +')
       (inc)
       (#(p/format-ratio % limit))))
