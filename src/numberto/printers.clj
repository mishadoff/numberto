(ns numberto.printers
  (:require [numberto.converters :as c])
  (:require [numberto.validator :as v])
  (:require [numberto.math :as m]))

(def default-properties 
  {:s 5 :e 5 :cnt true})

(defn format-num
  "Print long number in short format"
  ([num] (format-num num default-properties))
  ([num props]
     (v/validate num :integer :non-negative)
     (let [{s :s e :e cnt :cnt} (merge default-properties props)
           num-seq (c/num->digits num) n (count num-seq)]
       (if (< (+ s e) n)
         (str (apply str (take s num-seq))
              "..."
              (if cnt (str "[" (- n s e) "]...") "") 
              (apply str (drop (- n e) num-seq)))
         (str num)))))

(defn format-ratio 
  "Print ratio number with limit accuracy"
  [num limit]
  (v/validate num [#(or (ratio? %) (integer? %)) "Number should be ratio or integer"])
  (v/validate limit :integer)
  (let [[init-n init-d] (if (ratio? num) [(numerator num) (denominator num)] [num 1])
        numbers
        (loop [n init-n d init-d it -2 res []]
            (cond (= it limit) res
                  (= 0 n) res
                  (< n d) (recur (* 10 n) d (inc it) (conj res 0))
                  (>= n d) (recur (* 10 (mod n d)) d (inc it) (conj res (quot n d)))))]
    (-> numbers
        ((fn [ns] (into ns (repeat (- (+ limit 2) (count ns)) 0)))) ;; fill with zeros
        ((fn [ns] (let [f (if (>= (last ns) 5) inc identity)] ;; first rounding
                    (-> ns
                        (subvec 0 (dec (count ns)))
                        (update (- (count ns) 2) f)))))
        ((fn [ns]
           (loop [i (dec (count ns)) new-ns ns]
             (cond (or (< i 1) (< (nth new-ns i) 10)) new-ns
                   :else (recur (dec i) (-> new-ns
                                            (assoc i 0)
                                            (update (dec i) inc)))))))
        ((fn [[d & fs]] (str d (when-not (empty? fs) (apply str "." fs))))))))