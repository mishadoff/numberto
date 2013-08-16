(ns numberto.generators
  (:require [numberto.converters :as c]))

(defn rand-digit []
  "Return random digit [0-9]"
  (rand-int 10))

(defn rand-number [n]
  "Return random number with size of n"
  (->> (repeatedly n rand-digit)
       (apply str)
       bigint))
