(ns numberto.solvers-test
  (:require [clojure.test :refer :all]
            [numberto.solvers :as s]))

(def ^:private si s/solve-insert-ops)
(def ^:private sin s/solve-insert-ops-num)
(def ^:private sde s/solve-digit-equation)

(deftest solve-insert-ops-simple
  (is (= 5 (count (si [1 2]))))
  (is (= 3 (count (si [1 2] {:ops ["+" "-"]}))))
  (is (= 625 (count (si [1 2 3 4 5]))))
  (is (= 625 (count (si [1 2 3 4 5]))))
  (is (= [[0 "1-1"]] (sin [1 1] 0)))
  (is (= [[1 "1*1"] [1 "1/1"]] (sin [1 1] 1)))
  (is (= [[2 "1+1"]] (sin [1 1] 2)))
  (is (= [[11 "11"]] (sin [1 1] 11)))
  (is (empty? (sin [1 1] 12))))

(deftest solve-insert-ops-hard
  (is (= [[101 "3+((4*5)-6)*7"]] (sin [3 4 5 6 7] 101 {:parens 2})))
  (is (= [[35 "1^2+34"]] (sin [1 2 3 4] 35 {:ops ["+" "^"]}))))

(deftest solve-insert-ops-rules
  )

(deftest solve-insert-ops-corner-cases
  (is (= [[1 "1+0"] [1 "1-0"]] (sin [1 0] 1)))
  (is (= [[100 "100"]] (sin [100] 100)))
  (is (= [[1 "1+0"]] (sin [1 0] 1 {:ops ["+" "*"]})))
  (is (thrown? IllegalArgumentException (si [-1 1]))))

(deftest solve-digit-equation
  (is (= [["1 = 1" {"A" "1"}]] (sde "A=1")))
  (is (= [["1 = 1" {"A" "1"}]] (sde "A=1" :distinct? true)))
  (is (= 10 (count (sde "A = B" :distinct? false))))
  (is (= 0 (count (sde "A = B" :distinct? true))))
  (is (= ["0 = 0" {"A" "0" "B" "0"}] (first (sde "A = B" :distinct? false))))
  (is (= [["123+222 = 345" {"C" "2", "B" "2", "A" "2"}]]
         (sde "123+ABC=345" :distinct? false)))
  (is (= "27 * 37 = 999"(ffirst (sde "YE * ME = TTT"))))
  )
