# Converters

### Roman numbers

``` clojure
(number->roman 19) => "XIX"
```

``` clojure
(roman->number "MCMLXXXIX") => 1989
```

### Decimal to Ratio

Clojure has good Ratio type support. Using it is a good thing because you don't loss the accuracy. To get Ratio from your legacy double use `decimal->ratio`

``` clojure
(decimal->ratio 3.1415) => 6283/2000
```

If you want to get back from ratio to decimal, use standard converters

``` clojure
(double 1/2) => 0.5
(bigdec 1/2) => 0.5M
```

### Binary systems (more than 36)

[TODO]

# Factorial

### Stirling Approximation

Fast calculation approximate value of factorial

[TODO] implement
