(ns numberto.expression-test
  (:use [clojure.test])
  (:require [numberto.expression :as e]))

(def ^:private e e/eval-infix)

(deftest eval-infix-test
  (is (= 100500 (e "100500")))
  (is (= -3 (e "-3")))
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

(deftest eval-infix-bindings-test
  (is (= 120 (e "factorial(5)"
                {"factorial" #(reduce * (range 1 (inc %)))})))
  (is (= 3 (e "a+b" {"a" 1 "b" 2})))
  )
