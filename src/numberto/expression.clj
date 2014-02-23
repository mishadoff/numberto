(ns numberto.expression
  (:require [numberto.math :as m]))

;; set to true if you want to track parsing process
(def *DEBUG* (atom false))

;; can be replaced if you want to tweak algorithm
;; change priority, or add new operations
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

(defn- eval-error [symbol]
  (throw (IllegalArgumentException. (str "Binding for [" symbol "] is not provided"))))


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
       (apply str "\\w+|\\,|\\d+|\\(|\\)|") ;; adding numbers, symbols and parens
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
        functions (filter (fn [[_ tag _]] (= tag :function)) tokens)
        limit (dec (count tokens))
        ]
    (when @*DEBUG*
      (println "Input:" e)
      (println "Tagged Tokens:" tokens)
      (println "----------------------"))
    (loop [p 0 output [] opstack (list) funstack (list) arity []]
      (when @*DEBUG*
        (println "Output:" output)
        (println "Opstack:" opstack)
        (println "Funstack:" funstack)
        (println "Arity:" arity))
      (if (> p limit)
        ;; No more tokens
        (do 
          (when @*DEBUG*
            (println "----------------------"))
          (if (empty? opstack) {:postfix output :arity arity}
              (let [[_ tag _ :as triple] (peek opstack)]
                (when (= tag :left-paren) (parse-error))
                (recur p (conj output triple) (pop opstack) funstack arity))))
        ;; Token process
        (let [[op1 tag props :as triple] (nth tokens p)
              [_ prevtag _ :as prev] (if (> p 0) (nth tokens (dec p)) nil)]
          (when @*DEBUG*
            (println "Token: " triple)
            (println "----------------------"))
          (cond (or (= tag :number) (= tag :symbol))
                (recur (inc p) (conj output triple) opstack funstack arity)
                (= tag :function)
                (recur (inc p) output (conj opstack triple) (conj funstack [op1 1]) arity)
                (= tag :arg-separator)
                (let [[op2 tag2 _ :as all2] (peek opstack)
                      [f ar] (peek funstack)
                      new-funstack (conj (pop funstack) [f (inc ar)])]
                  (when-not all2 (parse-error)) ;; empty stack
                  (if (= :left-paren tag2)
                    (recur (inc p) output opstack new-funstack arity)
                    (recur p (conj output all2) (pop opstack) funstack arity)))
                (= tag :op)
                (let [[op2 _ _ :as all2] (peek opstack) props2 (@op-table op2)
                      [p1 p2] (map :priority [props props2])]
                  (if (and props2 (or (< p1 p2) (and (= :left (:assoc props)) (= p1 p2))))
                    (recur p (conj output all2) (pop opstack) funstack arity)
                    (recur (inc p) output (conj opstack triple) funstack arity)))
                (= tag :left-paren)
                (recur (inc p) output (conj opstack triple) funstack arity)
                (= tag :right-paren) ;; optimize ;; handle mismatches
                (let [[op2 tag2 _ :as all2] (peek opstack)]
                  (when-not all2 (parse-error)) ;; empty stack
                  (if (= :left-paren tag2)
                    (if (and (second opstack)
                             (= :function (second (second opstack))))
                      (let [[f ar] (peek funstack) newarity (if (= prevtag :left-paren) [f 0] [f ar])]
                        (recur (inc p) (conj output (second opstack)) (pop (pop opstack)) (pop funstack)
                               (conj arity newarity)))
                      (recur (inc p) output (pop opstack) funstack arity))
                    (recur p (conj output all2) (pop opstack) funstack arity)))
                :else (throw (IllegalArgumentException. "Not supported yet"))))))))

(defn- eval-postfix [postfix bindings]
  "Evaluates postfix expression. 1 2 + => 3"
  (let [raw-arities (:arity postfix) arities (apply hash-map (flatten raw-arities))]
    (if-not (= (count arities) (count raw-arities))
      (throw (IllegalArgumentException. "Functions overloading not supported yet")))
    (loop [[[token tag value :as triple] & tokens] (:postfix postfix) stack (list)]
      (if triple
        (cond (= :number tag) (recur tokens (conj stack value))
              (= :symbol tag)
              (let [value (bindings token)]
                (if-not value (eval-error token))
                (recur tokens (conj stack (bindings token))))
              (= :op tag) (recur tokens (conj (drop 2 stack)
                                              (apply (:function value) (reverse (take 2 stack)))))
              (= :function tag)
              (let [f (bindings token) arity (arities token)]
                (if-not f (eval-error token))
                (recur tokens (conj (drop arity stack)
                                    (apply f (reverse (take arity stack))))))
              :else (throw (IllegalArgumentException. "Invalid RPN")))
        (first stack))))) ;; more in stack?
  
(defn eval-infix 
  "Evaluate infix expression.
If functions or symbols are used, provide bindings map
"
  ([expr]
     (eval-infix expr {}))
  ([expr bindings]
     (eval-postfix (infix->postfix expr) bindings)))

(defn infix->prefix [expr]
  "Converts infix expression to lisp-style prefix expression
   Example: a + b * c => (+ a (* b c))"
  (let [postfix-map (infix->postfix expr)
        postfix (:postfix postfix-map)
        raw-bindings (:arity postfix-map)
        bindings (apply hash-map (flatten raw-bindings))]
    ;; validate arities
    (if-not (= (count bindings) (count raw-bindings))
      (throw (IllegalArgumentException. "Functions overloading not supported yet")))
    (loop [[[token tag value :as triple] & tokens] postfix stack (list)]
      (if triple
        (cond (= :number tag) (recur tokens (conj stack token))
              (= :symbol tag) (recur tokens (conj stack token))
              (= :op tag) (recur tokens (conj (drop 2 stack)
                                              (str "(" token " " (apply str (interpose " " (reverse (take 2 stack)))) ")")))
              (= :function tag)
              (let [arity (bindings token)]
                (recur tokens (conj (drop arity stack)
                                    (str "(" token " " (apply str (interpose " " (reverse (take arity stack)))) ")")))) 
               :else (throw (IllegalArgumentException. "Invalid RPN")))
        (first stack)))))

;; DONE Associativity
;; DONE custom functions
;; DONE Arity
;; DONE Arity guess
;; TODO Zero arity functions
;; TODO Unary operations
;; TODO Simplify multiple ops (* (* (* 1 2 3)))
;; TODO Handle arity in eval?
;; TODO Double numbers
;; TODO 2a
;; TODO Unary back
;; TODO infix errors
;; TODO rpn errors
;; TODO unbalanced parens
;; TODO prefix
;; TODO clean code
;; TODO tests
;; TODO avoid false positives
;; TODO Invalid token


;; Not supported \\w+ symbols
;; Not supported \\w+ functions
;; Shared place for symbol and function names
;; No arity overloading for functions
;; NUmber borders support
