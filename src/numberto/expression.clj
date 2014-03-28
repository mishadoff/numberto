(ns numberto.expression
  (:require [numberto.math :as m])
  (:require [numberto.validator :as v])
  (:require [numberto.factorial :as f])
  (:require [clojure.string :as s]))

;; set to true if you want to track parsing process
(def ^:dynamic *DEBUG* false)

(def configuration
  {:binary-ops
   {"+"   {:priority 10 :function + :assoc :left}
    "-"   {:priority 10 :function - :assoc :left}
    "*"   {:priority 20 :function * :assoc :left}
    "/"   {:priority 20 :function / :assoc :left}
    "^"   {:priority 30 :function m/power* :assoc :right}
    "**"  {:priority 30 :function m/power* :assoc :right}}

   :unary-ops
   {"-"   {:function -}}

   :bindings
   {;; Functions
    "cos"       #(Math/cos %)        
    "sin"       #(Math/sin %)
    "sum"       #(m/sum %&) 
    "max"       max
    "min"       min
    "avg"       #(m/avg %&)
    "log"       m/log
    "sqrt"      #(Math/sqrt %)
    
    ;; Symbols
    "e"         m/E
    "pi"        m/PI
    }})

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

(defn- tokenize [expr ops]
  "split expression into list of known tokens"
  (->> ops             ;; building tokenization regexp
       (sort-by count) ;; longest symbols comes first
       (reverse)       
       (interpose "|") 
       (apply str)
       (replace regexp-esc-map)
       (apply str "[a-zA-Z_][a-zA-Z_0-9]*|\\,|\\d+\\.\\d+|\\d+|\\(|\\)|")
       (re-pattern)
       (#(re-seq % expr))
       (validate-tokens expr)))

(defn tag-postprocess [tagged-tokens {:keys [unary-ops]}]
  (map (fn [[[_ left-tag _] [token tag v :as e] [_ right-tag _]]]
         (cond (unary-ops token) ;; to resolve ambiguities between unary and binary
               ;; Processing
               (cond (or (= :gap left-tag) ;; first token
                         (= :left-paren left-tag)
                         (= :binary left-tag))
                     [token :unary (assoc (unary-ops token) :arity 1)]
                     :else e)
               :else e))
       (partition 3 1 (concat [[nil :gap nil]]
                              tagged-tokens
                              [[nil :gap nil]]))))

(defn- tag [tokens {:keys [binary-ops unary-ops] :as conf}]
  "Tag the tokens type. Almost all tokens can be tagged
independently, except ones that need context.
Context captured for left and right neighbour.
Returns the triple [original value, tag, real value+meta]"
  (-> (map
       (fn [[left token right]]
         (cond (= (count (re-find #"\d+" token)) (count token))
               [token :number (bigint token)]
               (= (count (re-find #"\d+\.\d+" token)) (count token))
               [token :number (Double/parseDouble token)]
               (= "(" token) [token :left-paren \(]
               (= ")" token) [token :right-paren \)]
               (= "," token) [token :arg-separator \,]
               (binary-ops token) [token :binary (assoc (binary-ops token) :arity 2)]
               (unary-ops token) [token :unary (assoc (unary-ops token) :arity 1)]
               (= (count (re-find #"[a-zA-Z_][a-zA-Z_0-9]*" token)) (count token))
               (if (= "(" right) [token :function token] [token :symbol token])
               :else (token-error token)))
       (partition 3 1 (concat [:gap] tokens [:gap])))
      (tag-postprocess conf))) ;; handle unaries

(defn- infix->postfix [e {:keys [binary-ops unary-ops bindings] :as conf}]
  "Parse infix expression into Reverse Polish Notation.
Shunting-Yard algorithm. Supported operations defined in configuration var"
  (let [tokens (->> (concat (keys binary-ops) (keys unary-ops))
                    (tokenize e)
                    (#(tag % conf))
                    (vec))
        functions (filter (fn [[_ tag _]] (= tag :function)) tokens)
        limit (dec (count tokens))]
    (when *DEBUG*
      (println "Input:" e)
      (println "Tagged Tokens:" tokens)
      (println "----------------------"))
    (loop [p 0 output [] opstack (list) funstack (list) arity []]
      (when *DEBUG*
        (println "Output:" output)
        (println "Opstack:" opstack)
        (println "Funstack:" funstack)
        (println "Arity:" arity))
      (if (> p limit) ;; No more tokens
        (do (when *DEBUG*
              (println "----------------------"))
            (if (empty? opstack)
              ;; Postprocess to apply function arities
              (let [arity (atom arity)] ;; TODO remove atom hack 
                (mapv (fn [[name tag val :as triple]]
                        (if (= :function tag)
                          (let [ar (second (first @arity))]
                            (swap! arity rest)
                            [name tag {:name name :arity ar}])
                          triple)) output))
              (let [[_ tag _ :as triple] (peek opstack)]
                (if (= tag :left-paren) (parse-error))
                (recur p (conj output triple) (pop opstack) funstack arity))))
        ;; Token processing
        (let [[op1 tag props :as triple] (nth tokens p)
              [_ prevtag _ :as prev] (if (> p 0) (nth tokens (dec p)) nil)]
          (when *DEBUG*
            (println "Token: " triple)
            (println "----------------------"))
          (condp contains? tag
            #{:number :symbol}
            (recur (inc p) (conj output triple) opstack funstack arity)
            
            #{:function}
            (recur (inc p) output (conj opstack triple) (conj funstack [op1 1]) arity)

            #{:unary :left-paren}
            (recur (inc p) output (conj opstack triple) funstack arity)
            
            #{:binary}
            (let [[op2 _ _ :as all2] (peek opstack) props2 (binary-ops op2)
                  [p1 p2] (map #(get % :priority 1) [props props2])] ;; 1 is default priority
              (if (and props2
                       (or (< p1 p2)
                           (and (= :left (get props :assoc :left)) ;; :left is default associativity
                                (= p1 p2))))
                (recur p (conj output all2) (pop opstack) funstack arity)
                (recur (inc p) output (conj opstack triple) funstack arity)))
            
            #{:arg-separator}
            (let [[op2 tag2 _ :as all2] (peek opstack)
                  [f ar] (peek funstack)
                  new-funstack (conj (pop funstack) [f (inc ar)])]
              (when-not all2 (parse-error)) ;; empty stack
              (if (= :left-paren tag2)
                (recur (inc p) output opstack new-funstack arity)
                (recur p (conj output all2) (pop opstack) funstack arity)))
            
            #{:right-paren}
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
            
            (v/throw-iae "Not supported yet")))))))

(defn- eval-postfix [postfix bindings]
  "Evaluates postfix expression. 1 2 + => 3"
  (let [take-and-drop (fn [n stack f]
                        (->> (take n stack)
                             (reverse)
                             (apply f)
                             (conj (drop n stack))))]
    (loop [[[token tag {:keys [arity function] :as value} :as triple] & tokens] postfix stack (list)]
      (if triple
        (condp contains? tag
          #{:number}
          (recur tokens (conj stack value))
          #{:symbol}
          (let [value (bindings token)]
            (if-not value (eval-error token))
            (recur tokens (conj stack (bindings token))))
          #{:binary :unary}
          (recur tokens (take-and-drop arity stack function))
          #{:function}
          (let [f (bindings token) arity (:arity value)]
            (if-not f (eval-error token))
            (recur tokens (take-and-drop arity stack f)))

          (v/throw-iae "Invalid RPN"))
        (first stack))))) ;; more in stack?

(defn eval-infix 
  "Evaluate infix expression.
If functions or symbols are used, provide bindings map"
  ([expr]
     (eval-infix expr configuration))
  ([expr bindings]
     (let [conf (merge-with merge configuration bindings)]
       (eval-postfix (infix->postfix expr conf) (:bindings conf)))))

(defn infix->prefix 
  "Converts infix expression to lisp-style prefix expression
   Example: a + b * c => (+ a (* b c))"
  ([expr]
     (infix->prefix expr configuration))
  ([expr conf]
     (let [conf (merge-with merge configuration conf)
           postfix (infix->postfix expr conf)
           take-and-drop (fn [n stack token]
                           (->> (take n stack)
                                (reverse)
                                (interpose " ")
                                (apply str)
                                (#(str "(" token (if (pos? n) " " "") % ")")) ;; handle no arg functions
                                (conj (drop n stack))))]
       (loop [[[token tag value :as triple] & tokens] postfix stack (list)]
         (if triple
           (condp contains? tag
             #{:number :symbol}
             (recur tokens (conj stack token))
             #{:binary :unary :function}
             (recur tokens (take-and-drop (:arity value) stack token))

             (v/throw-iae (str "Invalid TOKEN=" triple)))
           (first stack)))))) ;;handle stack

;; TODO infix errors
;; TODO rpn errors
;; TODO unbalanced parens
;; TODO no more tokens in stack
;; TODO non-empty stack
;; TODO pow floats
;; TODO 2a+3
