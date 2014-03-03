(ns numberto.expression
  (:require [numberto.math :as m])
  (:require [numberto.validator :as v])
  (:require [clojure.string :as s]))

;; set to true if you want to track parsing process
(def *DEBUG* (atom false))

(def op-table
  {"+"   {:priority 1 :function + :assoc :left :arity 2}
   "-"   {:priority 1 :function - :assoc :left :arity 2}
   "*"   {:priority 2 :function * :assoc :left :arity 2}
   "/"   {:priority 2 :function / :assoc :left :arity 2}
   "^"   {:priority 3 :function m/power* :assoc :right :arity 2}
   "**"  {:priority 3 :function m/power* :assoc :right :arity 2}
  })

(defn- parse-error []
  (v/throw-iae "Invalid expression"))

(defn- eval-error [symbol]
  (v/throw-iae (str "Binding for [" symbol "] is not provided")))

(defn- token-error [tokens]
  (v/throw-iae (str "Cannot tokenize symbols " tokens)))

(def ^:private regexp-esc-map
  "regexp escape map"
  (let [esc-chars "()[]{}*+/&^$%#!"]
    (->> esc-chars
         (map #(str "\\" %))
         (zipmap esc-chars))))

(defn- validate-tokens [expr tokens]
  "return tokens if expr is fully parsed, otherwise IAE is thrown"
  (let [not-parsed (reduce #(s/replace-first %1 %2 "") expr tokens)]
    (if (empty? (s/trim not-parsed)) tokens
        (token-error not-parsed))))

(defn- tokenize [expr]
  "split expression into list of known tokens"
  (->> (keys op-table) ;; building tokenization regexp
       (sort-by count) ;; longest symbols comes first
       (reverse)       ;; to build correct
       (interpose "|") ;; based on op-table var
       (apply str)
       (replace regexp-esc-map)
       (apply str "[a-zA-Z_][a-zA-Z_0-9]*|\\,|\\d+\\.\\d+|\\d+|\\(|\\)|")
       (re-pattern)
       (#(re-seq % expr))
       (validate-tokens expr)))

(defn- tag [tokens]
  "Tag the tokens type. Almost all tokens can be tagged
independently, except ones that need context.
Context captured for left and right neighbour.
Returns the triple [original value, tag, real value+meta]"
  (map
   (fn [[left token right]]
     (cond (= (count (re-find #"\d+" token)) (count token))
           [token :number (bigint token)]
           (= (count (re-find #"\d+\.\d+" token)) (count token))
           [token :number (Double/parseDouble token)]
           (= "(" token) [token :left-paren \(]
           (= ")" token) [token :right-paren \)]
           (= "," token) [token :arg-separator \,]
           (op-table token) [token :op (op-table token)]
           (= (count (re-find #"[a-zA-Z_][a-zA-Z_0-9]*" token)) (count token))
           (if (= "(" right) [token :function token] [token :symbol token])
           :else (token-error token)))
   (partition 3 1 (concat [:gap] tokens [:gap]))))

(defn- infix->postfix [e]
  "Parse infix expression into Reverse Polish Notation.
Shunting-Yard algorithm. Supported operations defined in the op-table"
  (let [tokens (vec (tag (tokenize e)))
        functions (filter (fn [[_ tag _]] (= tag :function)) tokens)
        limit (dec (count tokens))]
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
      (if (> p limit) ;; No more tokens
        (do (when @*DEBUG*
              (println "----------------------"))
            (if (empty? opstack) {:postfix output :arity arity}
                (let [[_ tag _ :as triple] (peek opstack)]
                  (if (= tag :left-paren) (parse-error))
                  (recur p (conj output triple) (pop opstack) funstack arity))))
        ;; Token processing
        (let [[op1 tag props :as triple] (nth tokens p)
              [_ prevtag _ :as prev] (if (> p 0) (nth tokens (dec p)) nil)]
          (when @*DEBUG*
            (println "Token: " triple)
            (println "----------------------"))
          (cond (or (= tag :number)
                    (= tag :symbol))
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
                (let [[op2 _ _ :as all2] (peek opstack) props2 (op-table op2)
                      [p1 p2] (map :priority [props props2])]
                  (if (and props2 (or (< p1 p2) (and (= :left (:assoc props)) (= p1 p2))))
                    (recur p (conj output all2) (pop opstack) funstack arity)
                    (recur (inc p) output (conj opstack triple) funstack arity)))

                (= tag :left-paren)
                (recur (inc p) output (conj opstack triple) funstack arity)

                (= tag :right-paren) 
                (let [[op2 tag2 _ :as all2] (peek opstack)]
                  (when-not all2 (parse-error))
                  (if (= :left-paren tag2)
                    (if (and (second opstack)
                             (= :function (second (second opstack))))
                      (let [[f ar] (peek funstack) newarity (if (= prevtag :left-paren) [f 0] [f ar])]
                        (recur (inc p) (conj output (second opstack)) (pop (pop opstack)) (pop funstack)
                               (conj arity newarity)))
                      (recur (inc p) output (pop opstack) funstack arity))
                    (recur p (conj output all2) (pop opstack) funstack arity)))

                :else (v/throw-iae "Not supported yet")))))))

(defn- eval-postfix [postfix bindings]
  "Evaluates postfix expression. 1 2 + => 3"
  (let [raw-arities (:arity postfix) arities (apply hash-map (flatten raw-arities))]
    (if-not (= (count arities) (count raw-arities))
      (v/throw-iae "Functions overloading not supported yet"))
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
              :else (v/throw-iae "Invalid RPN"))
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
        bindings (apply hash-map (flatten raw-bindings))
        take-and-drop (fn [n stack token]
                        (->> (take n stack)
                             (reverse)
                             (interpose " ")
                             (apply str)
                             (#(str "(" token " " % ")"))
                             (conj (drop n stack))))]
    ;; validate arities
    (if-not (= (count bindings) (count raw-bindings))
      (v/throw-iae "Functions overloading not supported yet"))
    (loop [[[token tag value :as triple] & tokens] postfix stack (list)]
      (if triple
        (cond (or (= :number tag)
                  (= :symbol tag))
              (recur tokens (conj stack token))
              (= :op tag)
              (recur tokens (take-and-drop (:arity value) stack token))
              (= :function tag)
              (recur tokens (take-and-drop (bindings token) stack token))
              :else (v/throw-iae "Invalid RPN"))
        (first stack))))) ;; Handle stack

;; DONE Associativity
;; DONE custom functions
;; DONE Arity
;; DONE Arity guess
;; DONE Zero arity functions
;; DONE prefix
;; DONE Double numbers
;; DONE Invalid token
;; TODO infix errors
;; TODO rpn errors
;; TODO unbalanced parens
;; TODO clean code
;; TODO tests
;; TODO avoid false positives
;; TODO Code duplication

;;;;;;;;;;;;;;;;;;;;
;; Not included ;;;;
;;;;;;;;;;;;;;;;;;;;
;; No unary support
;; TODO Simplify multiple ops (* (* (* 1 2 3)))
;; TODO Unary operations
;; TODO Unary back
;; TODO 2a
;; Not supported \\w+ symbols
;; Not supported \\w+ functions
;; Shared place for symbol and function names
;; No arity overloading for functions
;; Bigintegers and Double
