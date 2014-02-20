(ns numberto.validator-test
  (:use [clojure.test])
  (:require [numberto.validator :as v]))

(deftest validate-test
  (is (nil? (v/validate 10 :integer)))
  (is (nil? (v/validate -1/2 :number :negative)))
  (is (thrown? IllegalArgumentException (v/validate 100 #(< % 95))))
  (is (nil? (v/validate 100 [#(< % 101) (comp not string?) [:positive "Very Positive"] #(= 10 (* % %))]))))

