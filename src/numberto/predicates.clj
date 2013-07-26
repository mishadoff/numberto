(ns numberto.predicates)

(defn digit? [digit]
  "Test whether number is one-digit [0-9]"
  (and (integer? digit) (<= 0 digit 9)))
