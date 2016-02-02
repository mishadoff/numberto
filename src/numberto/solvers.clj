(ns numberto.solvers
  (:require [clojure.string :as str]
            [numberto.converters :as c]
            [numberto.expression :as e]
            [numberto.validator  :as v]
            [numberto.math       :as m]))

(defn- binary-split 
  "Build splits from numbers and binary code indicates gaps
binary-split [1 2 3 4] [:gap :none :gap]) => [1 23 4]"
  [numbers code]
  (->> (cons nil code)
       (#(interleave % numbers))
       (rest)
       (partition-by #(= :gap %))
       (map #(remove (fn [e] (or (= :gap e)
                                 (= :none e))) %))
       (remove empty?)
       (map c/digits->num)))

;; TODO move fill zeros to converters
(defn- fill-zeros
  "Add zeros before number to create length of n
Example: ((fill-zeros 5) 123) => 00123"
  [n]
  (partial format (str "%0" n "d")))

(defn- splits 
  "Build all possible splits for a vector of numbers
Example: (splits [1 2 3]) => [[123] [12 3] [1 23] [1 2 3]]"
  [numbers]
  (let [n (dec (count numbers))
        limit (m/power* 2 n)]
    (if (zero? n) [numbers]
        (for [i (range limit)]
          (->> (str i)
               (#(c/radix-convert % 10 2))
               (biginteger)
               ((fill-zeros n))
               (seq)
               (map #({\0 :none \1 :gap} %))
               (binary-split numbers))))))

(defn- reverse-lookup
  "Create a map where keys are vals and vice verca.
If duplicated vals are present, result undefined"
  [m]
  (into {} (map (fn [[a b]] [b a]) m)))

(defn- valid-permute? 
  "Check whether generated code for split matches the rules"
  [code ops rules]
  (let [reverse-ops (reverse-lookup ops)]
    (loop [[r & rs] rules res true]
      (if (false? res) false
          (if r (condp contains? (first r) ;; match tags
                  #{:max :min}
                  (let [[tag op val] r k (reverse-ops op)]
                    (recur rs (({:max <= :min >=} tag)
                               (count (filter #(= k %) code)) val)))
                  #{:max-in-a-row :min-in-a-row}
                  (let [[tag op val] r k (reverse-ops op)]
                    (recur rs (->> (partition-by identity code)
                                   (filter #(= k (first %)))
                                   (map count)
                                   (map #(({:max-in-a-row <=
                                            :min-in-a-row >=} tag) % val))
                                   (reduce #(and %1 %2) true))))
                  (recur rs true))
              true)))))

;; TODO duplicates?
(defn- insert-parens
  "Generate all possible combinations of expression
with parens up to desired level. Keep level small."
  [level expr]
  (let [cnt (count expr)]
    (if (or (< cnt 5) (= level 0))
      nil ;; no way to insert parens
      (let [subidxs (->> (range (inc cnt))
                         (partition 4 2)
                         (map (fn [[a _ _ b]] [a b])))
            parens-exprs (map (fn [[start end]]
                                (concat
                                 (take start expr)
                                 [(concat ["("]
                                          (take (- end start)
                                                (drop start expr))
                                          [")"])]
                                 (drop end expr))) subidxs)]
        (->> parens-exprs
             (map #(insert-parens (dec level) %))
             (apply concat parens-exprs)
             (remove nil?))))))

(defn- permute-ops 
  "Generate all possible combinations of operations for current split"
  [split ops conf]
  (let [n (count split)]
    (if (= 1 n) [(str (first split))] ;; TODO check rules for min
        
        (flatten
         (for [i (->> (count ops) (#(m/power* % (dec n))) (range))
               :let [code (->> (str i)
                               (#(c/radix-convert % 10 (count ops)))
                               (biginteger)
                               ((fill-zeros (dec n)))
                               (seq))]
               :when (valid-permute? code ops (:rules conf))]
           (->> (map ops code)
                (cons nil)
                (#(interleave % split))
                (rest)
                (#(cons % (insert-parens (:parens conf) %)))
                (map flatten)
                (map #(apply str %))))))))

(defn solve-insert-ops
  "Find all possible values which could be obtained
 by inserting math operations between numbers"
  ([numbers]
     (solve-insert-ops numbers {}))
  ([numbers conf]
     (v/validate numbers [#(every? false? (map neg? %)) "numbers mut be non-negative"])
     (let [conf (merge {:ops      ["+" "-" "*" "/"]
                        :parens   0
                        :rules    []} conf)
           mapops (->> (count (:ops conf))
                       (range 0)
                       (apply str)
                       (seq)
                       (#(zipmap % (:ops conf))))]
       (v/validate (count (:ops conf)) [#(<= 2 % 9) "Number of operations must be in range [2..9]"])
       (->> (map #(try (let [ei (e/eval-infix %)]
                         [ei %])
                       (catch ArithmeticException e nil))
                 (mapcat #(permute-ops % mapops conf) (splits numbers)))
            (remove nil?)))))

(defn solve-insert-ops-num
  "Check whether inserting operations can yield some number [result]"
  ([numbers result]
     (solve-insert-ops-num numbers result {}))
  ([numbers result conf]
     (filter #(= result (first %)) (solve-insert-ops numbers conf))))

(defn solve-digit-equation
  "Solve Digit Exression

  Assuming you have a numeric equation with a digits
  hidden by a latin character and you need
  to find the origin of expression.

  Examples:
  A+B=C      => [\"1+2=3\"    {\"A\" : 1, \"B\" : 2, \"C\" : 3}, etc.]
  A1+B2=7C   => [\"11+62=73\" {\"A\" : 1, \"B\" : 6, \"C\" : 3}, etc.]

  Note: Some expression could have a multiple solutions

  Distinct flag indicates that you can not use the same digit
  for different letters
  
  "
  [equation & {:keys [distinct?] :or {distinct? true}}]
  (let [error-message
        "Equation should be in form <left-side> = <right-side>"]
    (v/validate equation
                [:string error-message]
                [#(re-matches #".+=.+" %) error-message])
    (let [[e1 e2 :as split-sides] (mapv str/trim (str/split equation #"="))
          unknown-characters (distinct (re-seq #"[a-zA-Z]" equation))
          permute-cardinality (count unknown-characters)
          replace-mappings (fn [s mappings]
                             (reduce (fn [acc [k v]]
                                       (str/replace acc k v))  s mappings))]
      ;; (println "Unknown characters" unknown-characters)
      (v/validate split-sides [#(= 2 (count %)) error-message])
      (v/validate
       unknown-characters
       [#(<= (count %) 6)]
       "Only 6 distinct unknown characters allowed")
      ;;(println "Permute cardinality " permute-cardinality)
      (for [i (range (m/power 10 permute-cardinality))
            :let [mapping (zipmap unknown-characters
                                  (->> (format (str "%0" permute-cardinality "d") i)
                                       (map c/char->digit)
                                       (map str)))
                  e1-expression (replace-mappings e1 mapping)
                  e2-expression (replace-mappings e2 mapping)]
            :when
            (cond
              distinct?
              (and (= permute-cardinality (count (distinct (vals mapping))))
                   (= (e/eval-infix e1-expression) (e/eval-infix e2-expression)))
              :else
              (= (e/eval-infix e1-expression) (e/eval-infix e2-expression)))]
        [(format "%s = %s" e1-expression e2-expression) mapping]))))
