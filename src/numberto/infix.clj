(ns numberto.infix)

(def op-table
  {"*" {:precedence 2 :function *}
   "/" {:precedence 2 :function /}
   "+" {:precedence 1 :function +}
   "-" {:precedence 1 :function -}
   }
  )

(defn tag [tokens]
  (map (fn [token]
         (cond (= (count (re-find #"\d+" token))
                  (count token)) [token :number]
                  (not (nil? (op-table token))) [token :fun]
                  (= "(" token) [token :paren-left]
                  (= ")" token) [token :paren-right]
                  :else [token :invalid]
                  )) tokens))

(defn parse [e]
  "Parse and evaluates expression.
Shunting-Yard algorithm
Currently +, -, *, / and parens are supported
"
  ;; TODO build tokenizator patter dynamically based on settings
  (let [tokens (vec (tag (re-seq #"\d+|\+|\-|\*|\/|\(|\)" e)))
        limit (dec (count tokens))]
    (println tokens)
    (loop [p 0 output [] opstack (list)]
      (println "Pointer: " p)
      (println "Output: " output)
      (println "Opstack: " opstack)
      (println "---------------")
      (if (> p limit)
        ;; No more tokens
        (if (empty? opstack) output
            (recur p (conj output (peek opstack)) (pop opstack))
            )
        ;; Token process
        (let [[op1 tag :as pair] (nth tokens p)]
          (println "\tOp:" op1 "Tag:" tag)
          (cond (= tag :number)
                (recur (inc p) (conj output op1) opstack)
                (= tag :fun)
                (let [op2 (peek opstack) ;; optimize
                      prec1 (:precedence (op-table op1))
                      prec2 (:precedence (op-table op2))]
                  (if (and prec2 (<= prec1 prec2))
                    (recur p (conj output op2) (pop opstack))
                    (recur (inc p) output (conj opstack op1))))
                (= tag :paren-left)
                (recur (inc p) output (conj opstack tag))
                (= tag :paren-right) ;; optimize ;; handle mismatches
                (let [op2 (peek opstack)]
                  (if (= :paren-left op2)
                    (recur (inc p) output (pop opstack))
                    (recur p (conj output op2) (pop opstack))))
                :else (throw (IllegalArgumentException. "Not supported yet"))))))))
