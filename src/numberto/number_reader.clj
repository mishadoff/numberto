(ns numberto.number-reader
  (:require [numberto.validator :as v]
            [numberto.math :as m]
            [numberto.converters :as c]))

;; Transforms number into human readable word representation
;; Supports English, Ukrainian and Russian
;; Aware about plurals in english and transformations in Ukr/Ru

(defn ->english-word
  "Builds a transformation map for the word for counting in English
   Handle plural and singular forms

   If one word is provided, it will be used for both plural and singular
     e.g. \"sheep\" -> one sheep, two sheep
   Plural ending could be specified in parens
     e.g. \"bird(s)\" -> one bird, two birds
   Or for two different words
     e.g. \"mouse|mice\" -> one mouse, two mice
   "
  [word]
  (let [[_ a1 _ a2] (re-matches #"([^()|]+)(\((.*)\))?" word)
        [_ b1 b2] (re-matches #"([^()|]+)\|([^()|]+)" word)]
    (cond a1 {:type :default :base a1 :default-ending a2 :endings [[1 ""]]}
          b1 {:type :default :base "" :default-ending b2 :endings [[1 b1]]}
          :else "")))

(defn ->cyrillic-word
  "Simple DSL to build a transformations map for the word
  For example \"грив(ень|ня|ні)\" means
    the base is \"грив\", default ending is \"ень\"
    for count 1 second modification is used \"ня\"
    for counts 2 3 4 third modification is used \"ні\"
    for all other counts, default ending is applied

  Some parts of word could be missing, e.g.
    \"метро\" - means no transformation applied to the word

  End of the word could optionally indicate type
    !f - female
    !m - male
    !i - it
  "
  [word]
  (let [clean (fn [s] (if (or (empty? s) (= "_" s)) "" s))
        [_ base _ end1 end2 end3 _ type]
        (re-matches #"([^|()]+)(\(([^|]*)\|([^|]*)\|([^|]*)\))?(!(.))?" word)]
    {:type (get {"f" :female "i" :it} type :default)
     :base (clean base)
     :default-ending (clean end1)
     :endings [[1 (clean end2)] [[2 4] (clean end3)]]}))

(def ^:private number-names
  {0 {:en {:default "zero"}
      :ukr {:default "нуль"}
      :ru {:default "ноль"}}
   1 {:en {:default "one"}
      :ukr {:default "один" :female "одна" :it "одне"}
      :ru {:default "один" :female "одна" :it "одно"}}
   2 {:en {:default "two"}
      :ukr {:default "два" :female "дві"}
      :ru {:default "два" :female "две"}}
   3 {:en {:default "three"}
      :ukr {:default "три"}
      :ru {:default "три"}}
   4 {:en {:default "four"}
      :ukr {:default "чотири"}
      :ru {:default "четыре"}}
   5 {:en {:default "five"}
      :ukr {:default "п'ять"}
      :ru {:default "пять"}}
   6 {:en {:default "six"}
      :ukr {:default "шість"}
      :ru {:default "шесть"}}
   7 {:en {:default "seven"}
      :ukr {:default "сім"}
      :ru {:default "семь"}}
   8 {:en {:default "eight"}
      :ukr {:default "вісім"}
      :ru {:default "восемь"}}
   9 {:en {:default "nine"}
      :ukr {:default "дев'ять"}
      :ru {:default "девять"}}
   10 {:en {:default "ten"}
       :ukr {:default "десять"}
       :ru {:default "десять"}}
   11 {:en {:default "eleven"}
       :ukr {:default "одинадцять"}
       :ru {:default "одинадцать"}}
   12 {:en {:default "twelve"}
       :ukr {:default "дванадцять"}
       :ru {:default "двенадцать"}}
   13 {:en {:default "thirteen"}
       :ukr {:default "тринадцять"}
       :ru {:default "тринадцать"}}
   14 {:en {:default "fourteen"}
       :ukr {:default "чотирнадцять"}
       :ru {:default "четырнадцать"}}
   15 {:en {:default "fifteen"}
       :ukr {:default "п'ятнадцять"}
       :ru {:default "пятнадцать"}}
   16 {:en {:default "sixteen"}
       :ukr {:default "шістнадцять"}
       :ru {:default "шестнадцать"}}
   17 {:en {:default "seventeen"}
       :ukr {:default "сімнадцять"}
       :ru {:default "семнадцать"}}
   18 {:en {:default "eighteen"}
       :ukr {:default "вісімнадцять"}
       :ru {:default "восемнадцать"}}
   19 {:en {:default "nineteen"}
       :ukr {:default "дев'ятнадцять"}
       :ru {:default "девятнадцать"}}
   20 {:en {:default "twenty"}
       :ukr {:default "двадцять"}
       :ru {:default "двадцать"}}
   30 {:en {:default "thirty"}
       :ukr {:default "тридцять"}
       :ru {:default "тридцать"}}
   40 {:en {:default "forty"}
       :ukr {:default "сорок"}
       :ru {:default "сорок"}}
   50 {:en {:default "fifty"}
       :ukr {:default "п'ятдесят"}
       :ru {:default "пятьдесят"}}
   60 {:en {:default "sixty"}
       :ukr {:default "шістдесят"}
       :ru {:default "шестьдесят"}}
   70 {:en {:default "seventy"}
       :ukr {:default "сімдесят"}
       :ru {:default "семьдесят"}}
   80 {:en {:default "eighty"}
       :ukr {:default "вісімдесят"}
       :ru {:default "восемьдесят"}}
   90 {:en {:default "ninety"}
       :ukr {:default "дев'яносто"}
       :ru {:default "девяносто"}}
   100 {:en {:default "hundred"}
        :ukr {:default "сто"}
        :ru {:default "сто"}}
   200 {:ukr {:default "двісті"}
        :ru {:default "двести"}}
   300 {:ukr {:default "триста"}
        :ru {:default "триста"}}
   400 {:ukr {:default "чотириста"}
        :ru {:default "четыреста"}}
   500 {:ukr {:default "п'ятсот"}
        :ru {:default "пятьсот"}}
   600 {:ukr {:default "шістсот"}
        :ru {:default "шестьсот"}}
   700 {:ukr {:default "сімсот"}
        :ru {:default "семьсот"}}
   800 {:ukr {:default "вісімсот"}
        :ru {:default "восемьсот"}}
   900 {:ukr {:default "дев'ятсот"}
        :ru {:default "девятьсот"}}})

;; number means x3 zeros
(def ^:private big-names
  {1 {:en (->english-word "thousand")
      :ukr (->cyrillic-word "тисяч(_|а|і)!f")
      :ru (->cyrillic-word "тысяч(_|а|и)!f")}
   2 {:en (->english-word "million")
      :ukr (->cyrillic-word "мільйон(ів|_|и)!m")
      :ru (->cyrillic-word "миллион(ов|_|а)!m")}
   3 {:en (->english-word "billion")
      :ukr (->cyrillic-word "мільярд(ів|_|и)!m")
      :ru (->cyrillic-word "миллиард(ов|_|а)!m")}
   4 {:en (->english-word "trillion")
      :ukr (->cyrillic-word "трильйон(ів|_|и)!m")
      :ru (->cyrillic-word "триллион(ов|_|а)!m")}
   5 {:en (->english-word "quadrillion")
      :ukr (->cyrillic-word "квадрильйон(ів|_|и)!m")
      :ru (->cyrillic-word "квадриллион(ов|_|а)!m")}
   6 {:en (->english-word "quintillion")
      :ukr (->cyrillic-word "квінтильйон(ів|_|и)!m")
      :ru (->cyrillic-word "квинтиллион(ов|_|а)!m")}
   7 {:en (->english-word "sextillion")
      :ukr (->cyrillic-word "секстильйон(ів|_|и)!m")
      :ru (->cyrillic-word "секстиллион(ов|_|а)!m")}
   8 {:en (->english-word "septillion")
      :ukr (->cyrillic-word "септильйон(ів|_|и)!m")
      :ru (->cyrillic-word "септиллион(ов|_|а)!m")}
   9 {:en (->english-word "octillion")
      :ukr (->cyrillic-word "октильйон(ів|_|и)!m")
      :ru (->cyrillic-word "октиллион(ов|_|а)!m")}
   10 {:en (->english-word "ninillion")
       :ukr (->cyrillic-word "нонильйон(ів|_|и)!m")
       :ru (->cyrillic-word "нониллион(ов|_|а)!m")}
   11 {:en (->english-word "decillion")
       :ukr (->cyrillic-word "децильйон(ів|_|и)!m")
       :ru (->cyrillic-word "дециллион(ов|_|а)!m")}
   12 {:en (->english-word "undecillion")
       :ukr (->cyrillic-word "ундецильйон(ів|_|и)!m")
       :ru (->cyrillic-word "ундециллион(ов|_|а)!m")}
   13 {:en (->english-word "duodecillion")
       :ukr (->cyrillic-word "додецильйон(ів|_|и)!m")
       :ru (->cyrillic-word "додециллион(ов|_|а)!m")}
   14 {:en (->english-word "tredecillion")
       :ukr (->cyrillic-word "тредецильйон(ів|_|и)!m")
       :ru (->cyrillic-word "тредециллион(ов|_|а)!m")}
   15 {:en (->english-word "quattuordecillion")
       :ukr (->cyrillic-word "кваттуордецильйон(ів|_|и)!m")
       :ru (->cyrillic-word "кваттуордециллион(ов|_|а)!m")}
   16 {:en (->english-word "quindecillion")
       :ukr (->cyrillic-word "квіндецильйон(ів|_|и)!m")
       :ru (->cyrillic-word "квиндециллион(ов|_|а)!m")}
   17 {:en (->english-word "sexdecillion")
       :ukr (->cyrillic-word "седецильйон(ів|_|и)!m")
       :ru (->cyrillic-word "седециллион(ов|_|а)!m")}})

(def extra-words
  {"-" {:en "minus" :ukr "мінус" :ru "минус"}
   "." {:en "point" :ukr "крапка" :ru "точка"}
   "," {:en "comma" :ukr "кома" :ru "запятая"}})

(defn word-with-effective-ending [word last-num]
  (let [base (:base word)
        ending
        (some->> (:endings word)
                 (filter (fn [[k end]]
                           (cond
                             (and (number? k) (= k last-num)) end
                             (and (set? k) (k last-num)) end
                             (and (vector? k) (= 2 (count k)) (<= (first k) last-num (second k))) end
                             :else nil)))
                 (first)
                 (second))]
    (str (or base "") (or ending (:default-ending word) ""))))

(defn resolve-number-name [lang close preferred]
  (or (get-in number-names [close lang preferred])
      (get-in number-names [close lang :default])))

(defn number-name
  "Convert number to its word representation.
   10^51 max number supported. (which is greater than number of atoms in the world)

   Accept language: one of [:en :ukr :ru]
   Accept word structure for correct ending generation
   "
  [input & {:keys [lang word] :or {lang :en}}]
  (letfn [(closest [num]
            (->> number-names
                 (filter (fn [[_ v]] (get-in v [lang :default])))
                 (map first)
                 sort
                 reverse
                 (drop-while #(> % num))
                 first))
          (atomic [num elements & {:keys [preferred] :or {preferred :default}}]
            (loop [n num s elements last num]
              (if (zero? n)
                [(clojure.string/join " " s) last]
                (let [close (closest n)]
                  (recur (- n close)
                         (conj s (resolve-number-name lang close preferred))
                         close)))))
          (compound [i num]                                 ;; in ukr and first compound has its own words
            (let [pref (if (= i 1) :female :default)]
              (cond
                (and word (zero? i) (#{:ukr :ru} lang)) (atomic num [] :preferred (:type word))
                (#{:ukr :ru} lang) (atomic num [] :preferred pref)
                :else
                (let [k (quot num 100)]
                  (if (zero? k) (atomic num [] :preferred pref)
                                (atomic (mod num 100) [(get-in number-names [k lang :default])
                                                       (get-in number-names [100 lang :default])]
                                        :preferred pref))))))
          (bignum-indexed [i num]
            (let [[s last-num] (compound i num)
                  eff-num (if (= :en lang) input last-num)]
              (if-not (empty? s)
                (cond-> s
                        (pos? i) (str " " (word-with-effective-ending (get-in big-names [i lang]) eff-num))
                        (and word (zero? i)) (str " " (word-with-effective-ending word eff-num))))))]
    (cond
      (zero? input)
      (cond-> (get-in number-names [0 lang :default])
              word (str " " (word-with-effective-ending word 0)))
      (neg? input) (str (get-in extra-words ["-" lang])
                      " "
                      (number-name (- input) :lang lang :word word ))
      :else
      (->> input
           c/num->digits
           reverse
           (partition 3 3 [0 0 0])
           (map #(c/digits->num (reverse %)))
           (map-indexed bignum-indexed)
           reverse
           (remove nil?)
           (interpose " ")
           (apply str)))))