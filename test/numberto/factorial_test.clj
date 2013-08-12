(ns numberto.factorial-test
  (:use [clojure.test])
  (:require [numberto.factorial :as f]))

(deftest !-test
  (doseq [! [f/! f/!!]] 
    (is (= 1 (! 0)))
    (is (= 1 (! 1)))
    (is (= 120 (! 5)))
    (is (= 3628800 (! 10)))
    (is (= 30414093201713378043612608166064768844377641568960512000000000000N (! 50)))
    (is (thrown? IllegalArgumentException (! -3)))
    (is (thrown? IllegalArgumentException (! "hello")))
    (is (thrown? IllegalArgumentException (! 1.0)))))

