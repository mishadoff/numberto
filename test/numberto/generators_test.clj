(ns numberto.generators-test
  (:require [clojure.test :refer :all]
            [numberto.generators :as g]
            [numberto.converters :as c]
            ))

(deftest rand-digit-test
  (dotimes [_ 10]
    (is ((set (range 10)) (g/rand-digit)))))

(deftest rand-number-test
  (dotimes [_ 10]
    (is (= 100 (count (c/num->digits (g/rand-number 100)))))))

(deftest rand-bigint-test
  (let [long-num 100000000000000000000000000000000000000000000000000]
    (dotimes [_ 10]
      (is (< (g/rand-bigint long-num) long-num)))))
