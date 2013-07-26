(ns numberto.converters)

(defn char->digit [char]
  "cast char to digit"
  (- (int char) 48))

(defn digit->char [digit]
  "cast digit to char representation"
  (char (+ digit 48)))

(defn num->digits [num]
  "split a number to the list of digits "
  (map char->digit (seq (str num))))

(defn digits->num [digits]
  (read-string (apply str digits))) ;; TODO replace read-string
