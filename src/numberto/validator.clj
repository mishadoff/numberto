(ns numberto.validator)

(def validate-predicates 
  {:integer integer?
   :number number?
   :string string? 
   :positive pos?
   :negative neg?
   :non-negative (comp not neg?)
   :nil nil?
   :not-nil (comp not nil?)
   })

(defn throw-iae [message] (throw (IllegalArgumentException. message)))

(defn- validate-keyword
  ([e k message]
     (let [fun (get validate-predicates k)]
       (cond (nil? fun) (throw-iae (str "Not supported predicate [" k "]"))
             (fun e) nil ;; everything is ok
             :else (throw-iae message))))
  ([e k]
     (validate-keyword e k (str "[" e "] does not satisfy predicate [" k "]"))))

(defn- validate-fn
  ([e f message]
     (if-not (f e) (throw-iae message)))
  ([e f]
     (validate-fn e f (str "[" e "] does not satisfy fn [" f "]"))))


(defn validate
  "Adhoc validation logic to prevent silly mistakes.
If validation succesful nil is returned, otherwise - IllegalArgumentException.
Three modes are supported:

1. Keyword (validate 10 :integer) - Supported keyword is bound to some predicate.
2. Custom predicate (validate 10 #(< 5 % 15))
3. Custom message (validate 10 [:integer \"n must be an integer\"] :number)
"
  [e & rules]
  (doseq [r rules]
    (cond
     (keyword? r) (validate-keyword e r)
     (fn? r) (validate-fn e r)
     (vector? r) 
       (let [[k message] r]
         (cond (fn? k) (validate-fn e k message)
               (keyword? k) (validate-keyword e k message)
               :else (throw-iae (str "Not supported element [" k "]")))))))
           
