(ns numberto.number-reader-test
  (:require [clojure.test :refer :all]
            [numberto.number-reader :as n]))

(deftest number-name-default-eng-test
  (is (= "zero" (n/number-name 0)))
  (is (= "minus one hundred twenty three" (n/number-name -123)))
  (is (= "one" (n/number-name 1)))
  (is (= "two" (n/number-name 2)))
  (is (= "eleven" (n/number-name 11)))
  (is (= "seventeen" (n/number-name 17)))
  (is (= "one thousand one" (n/number-name 1001)))
  (is (= "twenty three thousand four hundred fifty six" (n/number-name 23456)))
  (is (= "one hundred ninety seven thousand eight hundred sixty" (n/number-name 197860)))
  (is (= "one million seventeen" (n/number-name 1000017)))
  (is (= "one decillion" (n/number-name (reduce *' (repeat 33 10))))))

(deftest number-name-default-ukr-test
  (is (= "нуль" (n/number-name 0 :lang :ukr)))
  (is (= "мінус сто двадцять три" (n/number-name -123 :lang :ukr)))
  (is (= "один" (n/number-name 1 :lang :ukr)))
  (is (= "два" (n/number-name 2 :lang :ukr)))
  (is (= "одинадцять" (n/number-name 11 :lang :ukr)))
  (is (= "сімнадцять" (n/number-name 17 :lang :ukr)))
  (is (= "одна тисяча один" (n/number-name 1001 :lang :ukr)))
  (is (= "двадцять три тисячі чотириста п'ятдесят шість" (n/number-name 23456 :lang :ukr)))
  (is (= "сто дев'яносто сім тисяч вісімсот шістдесят" (n/number-name 197860 :lang :ukr)))
  (is (= "один мільйон сімнадцять" (n/number-name 1000017 :lang :ukr)))
  (is (= "два децильйони" (n/number-name (reduce *' 2 (repeat 33 10)) :lang :ukr))))

(deftest number-name-default-ru-test
  (is (= "ноль" (n/number-name 0 :lang :ru)))
  (is (= "минус сто двадцать три" (n/number-name -123 :lang :ru)))
  (is (= "один" (n/number-name 1 :lang :ru)))
  (is (= "два" (n/number-name 2 :lang :ru)))
  (is (= "одинадцать" (n/number-name 11 :lang :ru)))
  (is (= "семнадцать" (n/number-name 17 :lang :ru)))
  (is (= "одна тысяча один" (n/number-name 1001 :lang :ru)))
  (is (= "двадцать три тысячи четыреста пятьдесят шесть" (n/number-name 23456 :lang :ru)))
  (is (= "сто девяносто семь тысяч восемьсот шестьдесят" (n/number-name 197860 :lang :ru)))
  (is (= "один миллион семнадцать" (n/number-name 1000017 :lang :ru)))
  (is (= "два дециллиона" (n/number-name (reduce *' 2 (repeat 33 10)) :lang :ru))))

(deftest plurals-eng-test

  (let [word (n/->english-word "sheep")]
    (is (= "one sheep" (n/number-name 1 :lang :en :word word)))
    (is (= "two sheep" (n/number-name 2 :lang :en :word word)))
    (is (= "five sheep" (n/number-name 5 :lang :en :word word)))
    (is (= "thirty eight sheep" (n/number-name 38 :lang :en :word word)))
    (is (= "seventy one sheep" (n/number-name 71 :lang :en :word word))))
  (let [word (n/->english-word "bird(s)")]
    (is (= "one bird" (n/number-name 1 :lang :en :word word)))
    (is (= "two birds" (n/number-name 2 :lang :en :word word)))
    (is (= "five birds" (n/number-name 5 :lang :en :word word)))
    (is (= "thirty eight birds" (n/number-name 38 :lang :en :word word)))
    (is (= "seventy one birds" (n/number-name 71 :lang :en :word word))))
  (let [word (n/->english-word "mouse|mice")]
    (is (= "one mouse" (n/number-name 1 :lang :en :word word)))
    (is (= "two mice" (n/number-name 2 :lang :en :word word)))
    (is (= "five mice" (n/number-name 5 :lang :en :word word)))
    (is (= "thirty eight mice" (n/number-name 38 :lang :en :word word)))
    (is (= "seventy one mice" (n/number-name 71 :lang :en :word word)))))

(deftest plurals-ukr-test
  (let [word (n/->cyrillic-word "доллар(ів|_|и)!m")]
    (is (= "один доллар" (n/number-name 1 :lang :ukr :word word)))
    (is (= "два доллари" (n/number-name 2 :lang :ukr :word word)))
    (is (= "п'ять долларів" (n/number-name 5 :lang :ukr :word word)))
    (is (= "тридцять вісім долларів" (n/number-name 38 :lang :ukr :word word)))
    (is (= "сімдесят один доллар" (n/number-name 71 :lang :ukr :word word))))
  (let [word (n/->cyrillic-word "грив(ень|ня|ні)!f")]
    (is (= "одна гривня" (n/number-name 1 :lang :ukr :word word)))
    (is (= "дві гривні" (n/number-name 2 :lang :ukr :word word)))
    (is (= "п'ять гривень" (n/number-name 5 :lang :ukr :word word)))
    (is (= "тридцять вісім гривень" (n/number-name 38 :lang :ukr :word word)))
    (is (= "сімдесят одна гривня" (n/number-name 71 :lang :ukr :word word))))
  (let [word (n/->cyrillic-word "яблук(_|о|а)!i")]
    (is (= "одне яблуко" (n/number-name 1 :lang :ukr :word word)))
    (is (= "два яблука" (n/number-name 2 :lang :ukr :word word)))
    (is (= "п'ять яблук" (n/number-name 5 :lang :ukr :word word)))
    (is (= "тридцять вісім яблук" (n/number-name 38 :lang :ukr :word word)))
    (is (= "сімдесят одне яблуко" (n/number-name 71 :lang :ukr :word word))))
  (let [word {:type :it :base "" :default-ending "очей" :endings [[1 "око"] [[2 4] "ока"]]}]
    (is (= "одне око" (n/number-name 1 :lang :ukr :word word)))
    (is (= "два ока" (n/number-name 2 :lang :ukr :word word)))
    (is (= "п'ять очей" (n/number-name 5 :lang :ukr :word word)))
    (is (= "тридцять вісім очей" (n/number-name 38 :lang :ukr :word word)))
    (is (= "сімдесят одне око" (n/number-name 71 :lang :ukr :word word)))))

(deftest plurals-ru-test
  (let [word (n/->cyrillic-word "доллар(ов|_|а)!m")]
    (is (= "один доллар" (n/number-name 1 :lang :ru :word word)))
    (is (= "два доллара" (n/number-name 2 :lang :ru :word word)))
    (is (= "пять долларов" (n/number-name 5 :lang :ru :word word)))
    (is (= "тридцать восемь долларов" (n/number-name 38 :lang :ru :word word)))
    (is (= "семьдесят один доллар" (n/number-name 71 :lang :ru :word word))))
  (let [word (n/->cyrillic-word "грив(ен|на|ны)!f")]
    (is (= "одна гривна" (n/number-name 1 :lang :ru :word word)))
    (is (= "две гривны" (n/number-name 2 :lang :ru :word word)))
    (is (= "пять гривен" (n/number-name 5 :lang :ru :word word)))
    (is (= "тридцать восемь гривен" (n/number-name 38 :lang :ru :word word)))
    (is (= "семьдесят одна гривна" (n/number-name 71 :lang :ru :word word))))
  (let [word (n/->cyrillic-word "яблок(_|о|а)!i")]
    (is (= "одно яблоко" (n/number-name 1 :lang :ru :word word)))
    (is (= "два яблока" (n/number-name 2 :lang :ru :word word)))
    (is (= "пять яблок" (n/number-name 5 :lang :ru :word word)))
    (is (= "тридцать восемь яблок" (n/number-name 38 :lang :ru :word word)))
    (is (= "семьдесят одно яблоко" (n/number-name 71 :lang :ru :word word))))
  (let [word {:type :female :base "" :default-ending "жизней" :endings [[1 "жизнь"] [[2 4] "жизни"]]}]
    (is (= "одна жизнь" (n/number-name 1 :lang :ru :word word)))
    (is (= "две жизни" (n/number-name 2 :lang :ru :word word)))
    (is (= "пять жизней" (n/number-name 5 :lang :ru :word word)))
    (is (= "тридцать восемь жизней" (n/number-name 38 :lang :ru :word word)))
    (is (= "семьдесят одна жизнь" (n/number-name 71 :lang :ru :word word)))))