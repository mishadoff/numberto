(ns numberto.printers
  (:require [numberto.converters :as c])
  (:require [numberto.math :as m]))

(def default-properties 
  {:s 5
   :e 5
   :cnt true})

(defn format-num
  "Print long number in short format"
  ([num] (format-num num default-properties))
  ([num props]
     (let [{s :s e :e cnt :cnt} props
           num-seq (c/num->digits num) n (count num-seq)]
       (if (< (+ s e) n)
         (str (apply str (take s num-seq))
              "..."
              (if cnt (str "[" (- n s e) "]...") "") 
              (apply str (drop (- n e) num-seq)))
         (str num)))))

(defn what [num]
  "Extract some useful number properties"
  {:number num
   :count-of-digits (m/count-digits num)
   :sum-of-digits (m/sum-of-digits num)
   }
  )
