(ns numberto.expression-test
  (:use [clojure.test])
  (:require [numberto.expression :as e]))

(def ^:private e e/eval-infix)
(def ^:private p e/infix->prefix)

(deftest eval-infix-test-ops
  (is (= 100500 (e "100500")))
  (is (= -3 (e "-3")))
  (is (= -6 (e "-1-2-3")))
  (is (= 3 (e "1+2")))
  (is (= 2 (e "1-(2-3)")))
  (is (= 7 (e "1+2*3")))
  (is (= 9 (e "(1+2)*3")))
  (is (= 1/2 (e "1/2")))
  (is (= 1 (e "1^0")))
  (is (= 1/24 (e "1/2/3/4")))
  (is (= 256 (e "2^2^3")))
  (is (= 256 (e "2^(2^3)")))
  (is (= 64 (e "(2^2)^3")))
  (is (= 7 (e "1/(3^0/(2*3+1))")))
  (is (= 4.0 (e "1.666 + 2.334"))))

(deftest eval-infix-function-test
  (is (= 6 (e "sum(1,2,3)")))
  (is (= 2.0 (e "avg(1,2,3)")))
  (is (= 24 (e "sum() + sum(1,2) + sum(1,2,3,4,5,6)")))
  (is (= 0 (e "max(1,2) - min(2,3)")))
  (is (= 1.0 (e "cos(0) + sin(0)")))
  (is (= (e "log(2,4)") (e "sqrt(4)")))
  (is (= (e "e^0") (e "pi^0")))
  )


(deftest eval-infix-conf-test
  ;; functions and symbols
  (is (= 120 (e "factorial(5)" {:bindings {"factorial" #(reduce * (range 1 (inc %)))}})))
  (is (= 3 (e "a+b" {:bindings {"a" 1 "b" 2}})))
  (is (integer? (e "now()" {:bindings {"now" #(.getTime (java.util.Date.))}})))
  ;; binary op :function overloading
  (is (= -1 (e "1+2" {:binary-ops {"+" {:function -}}})))
  (is (= "123" (e "1+2+3" {:binary-ops {"+" {:function #(apply str [%1 %2])}}})))
  (is (= "222222222222" (e "2*3*4" {:binary-ops {"*" {:function #(apply str (repeat %2 %1))}}})))
  ;; binary op :priority overloading
  (is (= 7 (e "1+2*3")))
  (is (= 9 (e "1+2*3" {:binary-ops {"+" {:function + :priority 100}}})))
  (is (= 9  (e "1+2^3")))
  (is (= 27 (e "1+2^3" {:binary-ops {"+" {:function + :priority 100}}})))
  (is (= 13 (e "4*5/2+3")))
  (is (= 4  (e "4*5/2+3" {:binary-ops {"+" {:function + :priority 100}}})))
  (is (= 22 (e "4*5/2+3" {:binary-ops
                          {"+" {:function +  :priority 20}
                           "/" {:function /  :priority 30}
                           "*" {:function *  :priority 10}}})))
  ;; binary op :associativity overloading 
  (is (= -4 (e "1-2-3")))
  (is (= 2 (e "1-2-3" {:binary-ops {"-" {:function - :assoc :right}}})))
  ;; unary ops
  (is (= 2 (e "++1" {:unary-ops {"++" {:function inc}}})))
  (is (= 1 (e "--2" {:unary-ops {"--" {:function dec}}})))
  (is (= 6 (e "!3" {:unary-ops {"!" {:function #(reduce *' (range 1 (inc %)))}}})))
  (is (= 8 (e "**4" {:unary-ops {"**" {:function #(* % 2)}}})))
  (is (= 16 (e "#4" {:unary-ops {"#" {:function #(* % 4)}}})))
  )


(deftest infix->prefix-test-ops
  (is (= "(+ 1 2)" (p "1+2")))
  (is (= "(- 1 2)" (p "1-2")))
  (is (= "(- 2)" (p "-2")))
  (is (= "(- (- 1) 2)" (p "-1-2")))
  (is (= "(* (/ 2 a) 4)" (p "2/a*4")))
  (is (= "(/ 1 0)" (p "1/0")))
  ;; functions
  (is (= "(now)" (p "now()")))
  (is (= "(now 1)" (p "now(1)")))
  (is (= "(now 1 2)" (p "now(1, 2)")))
  (is (= "(now (now 1 2))" (p "now(now(1,2))")))
  ;; handle 1000 arguments
  (is (= (str "(f " (apply str (interpose " " (range 1000)))")")
         (p (str "f(" (apply str (interpose "," (range 1000))) ")"))))

  ;; TODO false when implement folding
  (is (= "(* (* (* 1 2) 3) 4)" (p "1 * 2 * 3 * 4")))
  )

(deftest infix->prefix-test-formulas
  ;; simpson's rule
  (is (= "(* (/ (- b a) 6) (+ (+ (f a) (* 4 (f (/ (+ a b) 2)))) (f b)))"
         (p "(b-a)/6*(f(a) + 4*f((a+b)/2) +f(b))")))
  )

(deftest eval-infix-broken
  (doseq [i ["1+", "+", "--", "1+("
             "1 2", "(2-)",  ")(1)"
             "1 2 3 4 + -+", "()"
             "+3", "-", "1007001^"
             "1 2 - 3", "6*7*7**"
             "avg(1,)", "2^(+12)"
             ]]
    (is (thrown? IllegalArgumentException (e i)))
    )
  )
