(ns numberto.validator)

(def validate-predicates 
  {:integer integer?
   :number number?
   :positive pos?
   :non-negative (comp not neg?)})

(defn validate [num & rules]
  (let [e #(throw (IllegalArgumentException. (str "num " %1 " must satisfy predicate " %2)))]
    (doseq [r rules]
      (cond
        (keyword? r) (if-not ((get validate-predicates r) num) (e num r))
        (fn? r) (if-not (r num) (e num r))
        :else (throw (IllegalArgumentException. "Not supported element"))))))
