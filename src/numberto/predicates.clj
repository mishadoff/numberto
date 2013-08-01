(ns numberto.predicates
  (:require [numberto.converters :as c])
  (:require [numberto.math :as m]))

(defn digit? [d]
  (c/digit? d))

(defn square? [n]
  (try 
    (m/square? n)
    (catch IllegalArgumentException e false)))
  
