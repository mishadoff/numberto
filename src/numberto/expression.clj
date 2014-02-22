(ns numberto.expression
  (:require [numberto.math :as m]))

;; set to true if you want to track parsing process
(def *DEBUG* (atom false))

;; can be replaced if you want to tweak algorithm
;; change priority, or add new functions
(def op-table
  (atom
   {"+" {:priority 1 :function + :assoc :left}
    "-" {:priority 1 :function - :assoc :left}
    "*" {:priority 2 :function * :assoc :left}
    "/" {:priority 2 :function / :assoc :left}
    "^" {:priority 3 :function m/power* :assoc :right}
    }))

(defn- parse-error []
  (throw (IllegalArgumentException. "Invalid expression")))

(def ^:private regexp-esc-map
  "regexp escape map"
  (let [esc-chars "()[]{}*+/&^$%#!"]
    (->> esc-chars
         (map #(str "\\" %))
         (zipmap esc-chars))))

(defn- tokenize [expr]
  "split expression into list of known tokens"
  (->> (keys @op-table) ;; building tokenization regexp
       (interpose "|") ;; based on op-table var
       (apply str)
       (replace regexp-esc-map)
       (apply str "\\w+|\\,|\\d+|\\(|\\)|") ;; adding numbers and parens
       (re-pattern)
       (#(re-seq % expr))))

(defn- tag [tokens]
  "Tag the tokens type. Almost all tokens can be tagged
independently, except ones that need context.
Context captured for left and right neighbour.
Available types:
[:number :left-paren :right-paren :function :funcall]
Returns the triple [original value, tag, real value]"
  (map
   (fn [[left token right]]
     (cond (= (count (re-find #"\d+" token)) (count token))
           [token :number (bigint token)]
           (= "(" token) [token :left-paren \(]
           (= ")" token) [token :right-paren \)]
           (= "," token) [token :arg-separator \,]
           (@op-table token)
           [token :op (@op-table token)]
           (= (count (re-find #"\w+" token)) (count token))
           (cond (= "(" right)
                 [token :function token]
                 :else [token :symbol token])
           :else (throw (IllegalArgumentException. (str "Unsupported token [" token "]")))))
   (partition 3 1 (concat [:gap] tokens [:gap]))))

(defn- infix->postfix [e]
  "Parse infix expression into Reverse Polish Notation.
Shunting-Yard algorithm
Supported operations defined in the atom @op-table
"
  (let [tokens (vec (tag (tokenize e)))
        limit (dec (count tokens))]
    (when @*DEBUG*
      (println "Input:" e)
      (println "Tagged Tokens:" tokens)
      (println "----------------------"))
    (loop [p 0 output [] opstack (list)]
      (when @*DEBUG*
        (println "Output:" output)
        (println "Opstack:" opstack))
      (if (> p limit)
        ;; No more tokens
        (if (empty? opstack) output
            (recur p (conj output (first (peek opstack))) (pop opstack)))
        ;; Token process
        (let [[op1 tag props :as triple] (nth tokens p)]
          (when @*DEBUG*
            (println "Token: " triple)
            (println "----------------------"))
          (cond (= tag :number)
                (recur (inc p) (conj output op1) opstack)
                (= tag :symbol)
                (recur (inc p) (conj output op1) opstack)
                (= tag :function)
                (recur (inc p) output (conj opstack triple))
                (= tag :arg-separator)
                (let [[op2 tag2 _ :as all2] (peek opstack)]
                  (when-not all2 (parse-error)) ;; empty stack
                  (if (= :left-paren tag2)
                    (recur (inc p) output opstack)
                    (recur p (conj output op2) (pop opstack))))
                (= tag :op)
                (let [[op2 _ _ :as all2] (peek opstack) props2 (@op-table op2)
                      [p1 p2] (map :priority [props props2])]
                  (if (and props2 (or (< p1 p2) (and (= :left (:assoc props)) (= p1 p2))))
                    (recur p (conj output op2) (pop opstack))
                    (recur (inc p) output (conj opstack triple))))
                (= tag :left-paren)
                (recur (inc p) output (conj opstack triple))
                (= tag :right-paren) ;; optimize ;; handle mismatches
                (let [[op2 tag2 _ :as all2] (peek opstack)]
                  (when-not all2 (parse-error)) ;; empty stack
                  (if (= :left-paren tag2)
                    (if (and (second opstack)
                             (= :function (second (second opstack))))
                      (recur (inc p) (conj output (first (second opstack))) (pop (pop opstack)))
                      (recur (inc p) output (pop opstack)))
                    (recur p (conj output op2) (pop opstack))))
                :else (throw (IllegalArgumentException. "Not supported yet"))))))))

(defn- eval-postfix [expr]
  "Evaluates postfix expression. 1 2 + => 3"
  (loop [[[_ t value :as all] & tokens] (map tag expr)
         stack (list)]
    (if all
      (cond (= :number t) (recur tokens (conj stack value))
            (= :op t) (recur tokens
                                   (conj (drop 2 stack)
                                         (apply (:function value) (reverse (take 2 stack)))))
            :else (throw (IllegalArgumentException. "Invalid RPN")))
      (first stack))))

(defn eval-infix [expr]
  "Evaluate infix expression"
  (eval-postfix (infix->postfix expr)))

(defn infix->prefix [expr]
  "Converts infix expression to lisp-style prefix expression
   Example: a + b * c => (+ a (* b c))"
  (let [postfix (tag (infix->postfix expr))]
    
    ))

;; DONE Associativity
;; TODO Unary back front
;; TODO infix errors
;; TODO rpn errors
;; TODO unbalanced parens
;; TODO prefix
;; TODO Debug
;; TODO custom functions
;; TODO Arity
;; TODO clean code
;; TODO tests
