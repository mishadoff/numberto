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

(deftest number-name-test
  (is (= "" (p/number-name 0)))
  (is (= "one" (p/number-name 1)))
  (is (= "seventeen" (p/number-name 17)))
  (is (= "one million seventeen" (p/number-name 1000017)))
  (is (= "fifty four thousand three hundred twenty one" 
         (p/number-name 54321)))
  (is (= "one duotrigintillion" 
         (p/number-name (reduce *' (repeat 99 10))))))
