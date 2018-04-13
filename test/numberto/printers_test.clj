(ns numberto.printers-test
  (:require [clojure.test :refer :all]
            [numberto.printers :as p]))

(deftest format-num-test
  (is (= "0" (p/format-num 0)))
  (is (= "12345...[10]...67890"
         (p/format-num 12345678901234567890)))
  (is (= "11111...[9990]...11111"
         (p/format-num (bigint (apply str (repeat 10000 1))))))
  (is (= "1...[7]...1" (p/format-num 123454321 {:s 1 :e 1})))
  (is (= "12345...54321" (p/format-num 1234567654321 {:cnt false})))
  (is (thrown? IllegalArgumentException (p/format-num [])))
  (is (thrown? IllegalArgumentException (p/format-num -1))))

(deftest format-ratio-test
  (is (= "0.3" (p/format-ratio 1/3 1)))
  (is (= "0.33" (p/format-ratio 1/3 2)))
  (is (= "0.67" (p/format-ratio 2/3 2)))
  (is (= "20.00" (p/format-ratio 100/5 2)))
  (is (= "1.00" (p/format-ratio 1 2)))
  (is (= "1.00" (p/format-ratio (- 1 1/10000000) 2)))
  (is (= "0.99" (p/format-ratio (- 1 1/100) 2)))
  (is (= "1" (p/format-ratio (- 1 1/10000000) 0)))
  (is (= "1.000000" (p/format-ratio (- 1 1/10000000) 6)))
  (is (thrown? IllegalArgumentException (p/format-ratio 0.5 2))))