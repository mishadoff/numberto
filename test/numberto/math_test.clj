(ns numberto.math-test
  (:use [clojure.test])
  (:require [numberto.math :as m]))

(deftest constants-test
  (is (= m/PI Math/PI))
  (is (= m/E Math/E)))

(deftest power-test
  (doseq [pow [m/power m/power*]]
    (is (= 16 (pow 2 4)))
    (is (= 16 (pow 4 2)))
    (is (= 1 (pow 100 0)))
    (is (= 0 (pow 0 100)))
    (is (= 1 (pow 0 0)))
    (is (thrown? IllegalArgumentException (pow "1" 2)))
    (is (thrown? IllegalArgumentException (pow 1 1.5)))
    (is (thrown? IllegalArgumentException (pow "300" "10")))
    (is (thrown? IllegalArgumentException (pow 4 -1)))))

(run-tests)

