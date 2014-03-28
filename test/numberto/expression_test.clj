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
  )


(deftest eval-infix-conf-test
  ;; functions and symbols
  (is (= 120 (e "factorial(5)" {:bindings {"factorial" #(reduce * (range 1 (inc %)))}})))
  (is (= 3 (e "a+b" {:bindings {"a" 1 "b" 2}})))
  (is (integer? (e "now()" {:bindings {"now" #(.getTime (java.util.Date.))}})))
  ;; binary op :function overloading
  (is (= -1 (e "1+2" {:binary-ops {"+" {:function - :assoc :left :priority 1}}})))
  (is (= "123" (e "1+2+3" {:binary-ops {"+" {:function #(apply str [%1 %2]) :assoc :left :priority 1}}})))
  (is (= "222222222222" (e "2*3*4" {:binary-ops {"*" {:function #(apply str (repeat %2 %1)) :assoc :left :priority 30}}})))
  ;; binary op :priority overloading
  (is (= 7 (e "1+2*3")))
  (is (= 9 (e "1+2*3" {:binary-ops {"+" {:function + :assoc :left :priority 100}}})))
  (is (= 9  (e "1+2^3")))
  (is (= 27 (e "1+2^3" {:binary-ops {"+" {:function + :assoc :left :priority 100}}})))
  (is (= 13 (e "4*5/2+3")))
  (is (= 4  (e "4*5/2+3" {:binary-ops {"+" {:function + :assoc :left :priority 100}}})))
  (is (= 22 (e "4*5/2+3" {:binary-ops
                          {"+" {:function + :assoc :left :priority 20}
                           "/" {:function / :assoc :left :priority 30}
                           "*" {:function * :assoc :left :priority 10}}})))
  ;; binary op :associativity overloading 
  (is (= -4 (e "1-2-3")))
  (is (= 2 (e "1-2-3" {:binary-ops {"-" {:function - :assoc :right :priority 100}}})))
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
  
  ;; TODO false when implement folding
  (is (= "(* (* (* 1 2) 3) 4)" (p "1 * 2 * 3 * 4")))
  )

(deftest infix->prefix-test-formulas

  )
