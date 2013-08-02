(ns numberto.validator)

(def validate-predicates 
  {:integer integer?
   :number number?
   :positive pos?
   :non-negative (comp not neg?)})

(defn validate [num & rules]
  (doseq [r rules]
    (if (not ((get validate-predicates r) num))
      (throw (IllegalArgumentException. (str "num must satisfy predicate " r))))))
