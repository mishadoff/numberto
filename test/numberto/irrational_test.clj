(ns numberto.irrational-test
  (:require [clojure.test :refer :all]
            [numberto.irrational :as i]))

(deftest e-test
  (is (= "3" (i/e :limit 0)))
  (is (= "2.72" (i/e :limit 2)))
  (is (= "2.718" (i/e :limit 3 :iterations 200)))
  (is (= (+ 100 2) (count (i/e :limit 100 :iterations 500)))))

(deftest pi-test
  (is (= "3" (i/pi :limit 0)))
  (is (= "3.14" (i/pi :limit 2)))
  (is (= "3.1416" (i/pi :limit 4)))
  (is (= (+ 100 2) (count (i/pi :limit 100 :iterations 500)))))

(deftest sqrt-test
  (is (= "1.41" (i/sqrt 2 :limit 2)))
  (is (= "15" (i/sqrt 225 :limit 0)))
  (is (= "15.00" (i/sqrt 225 :limit 2))))