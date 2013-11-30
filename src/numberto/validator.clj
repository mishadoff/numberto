(ns numberto.validator)

(def validate-predicates 
  {:integer integer?
   :number number?
   :string string?
   :positive pos?
   :non-negative (comp not neg?)})

(defn validate [e & rules]
  (let [exc #(throw (IllegalArgumentException. 
                     (str "element [" %1 "] must satisfy predicate " %2)))
        exc-message #(throw (IllegalArgumentException. 
                             (str "element [" %1 "] " %2)))]
    (doseq [r rules]
      (cond
       (keyword? r) (if-not ((get validate-predicates r) e) (exc e r))
       (fn? r) (if-not (r e) (exc e r))
       (vector? r) 
       (let [[fun message] r]
         (cond (and (fn? fun)
                    (string? message))
               (if-not (fun e) (exc-message e message))
               :else (throw (IllegalArgumentException. "Not supported element"))))                
       :else (throw (IllegalArgumentException. "Not supported element"))))))
