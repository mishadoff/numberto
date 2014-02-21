(ns numberto.expression
  (:require [numberto.math :as m]))

;; set to true if you want to track parsing process
(def ^:dynamic *DEBUG* false)

;; can be replaced if you want to tweak algorithm
;; change priority, or add new functions
(def ^:dynamic op-table
  {"+" {:priority 1 :function + :assoc :left}
   "-" {:priority 1 :function - :assoc :left}
   "*" {:priority 2 :function * :assoc :left}
   "/" {:priority 2 :function / :assoc :left}
   "^" {:priority 3 :function m/power* :assoc :right}
   }
  )

(def ^:private regexp-esc-map
  "regexp escape map"
  (let [esc-chars "()[]{}*+/&^$%#!"]
    (->> esc-chars
         (map #(str "\\" %))
         (zipmap esc-chars))))

(defn- tokenize [expr]
  "split expression into list of known tokens"
  (->> (keys op-table) ;; building tokenization regexp
       (interpose "|") ;; based on op-table var
       (apply str)
       (replace regexp-esc-map)
       (apply str "\\d+|\\(|\\)|") ;; adding numbers and parens
       (re-pattern)
       (#(re-seq % expr))))

(defn- tag [token]
  "Tag the token type.
Available types: [:number :left-paren :right-paren :function]
Returns the triple [original value, tag, real value]"
  (cond (= (count (re-find #"\d+" token)) (count token))
        [token :number (bigint token)]
        (= "(" token) [token :left-paren \(]
        (= ")" token) [token :right-paren \)]
        (op-table token)
        [token :function (op-table token)]
        :else (throw (IllegalArgumentException. (str "Unsupported token [" token "]")))))

(defn parse [e]
  "Parse and evaluates expression.
Shunting-Yard algorithm
Currently +, -, *, / and parens are supported
"
  ;; TODO build tokenizator pattern dynamically based on settings
  (let [tokens (vec (map tag (tokenize e)))
        limit (dec (count tokens))]
    (when *DEBUG*
      (println "Input:" e)
      (println "Tagged Tokens:" tokens)
      (println "----------------------"))
    (loop [p 0 output [] opstack (list)]
      (when *DEBUG*
        (println "Output:" output)
        (println "Opstack:" opstack))
      (if (> p limit)
        ;; No more tokens
        (if (empty? opstack) output
            (recur p (conj output (peek opstack)) (pop opstack)))
        ;; Token process
        (let [[op1 tag props :as triple] (nth tokens p)]
          (when *DEBUG*
            (println "Token: " triple)
            (println "----------------------"))
          (cond (= tag :number)
                (recur (inc p) (conj output op1) opstack)
                (= tag :function)
                (let [op2 (peek opstack) props2 (op-table op2)
                      [p1 p2] (map :priority [props props2])]
                  (if (and props2 (or (< p1 p2) (and (= :left (:assoc props)) (= p1 p2))))
                    (recur p (conj output op2) (pop opstack))
                    (recur (inc p) output (conj opstack op1))))
                (= tag :left-paren)
                (recur (inc p) output (conj opstack tag))
                (= tag :right-paren) ;; optimize ;; handle mismatches
                (let [op2 (peek opstack)]
                  (if (= :left-paren op2)
                    (recur (inc p) output (pop opstack))
                    (recur p (conj output op2) (pop opstack))))
                :else (throw (IllegalArgumentException. "Not supported yet"))))))))

(defn eval-rpn [rpn]
  (loop [[[_ t value :as all] & tokens] (map tag rpn) stack (list)]
    (if all
      (cond (= :number t) (recur tokens (conj stack value))
            (= :function t) (recur tokens
                                   (conj (drop 2 stack) ;; invalid rpn
                                         (apply (:function value) (reverse (take 2 stack)))))
            :else (throw (IllegalArgumentException. "Invalid RPN")))
      (first stack)) ;; if more in stack
    ))

;; DONE Associativity
;; TODO Unary back front
;; TODO infix errors
;; TODO rpn errors
;; TODO unbalanced parens
;; Right-to-Left
;; TODO prefix
;; TODO Debug
;; TODO custom functions
;; TODO Arity
;; TODO clean code
;; TODO tests
