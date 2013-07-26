(ns numberto.core-test
  (:use [clojure.test])
  (:require [numberto.core :as c]))

(deftest count-digits-test
  (is (= 3 (c/count-digits 345)))
  (is (= 1 (c/count-digits 0)))
  (is (= 7 (c/count-digits 1254562))))
  
  
