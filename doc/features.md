Features to be added

# Printers [NEW]

`(:use [numberto.printers])`

### Long numbers

If you have a *really* big number, printing it to `System.out` not a good idea.
`format-num` allows you to capture important property of number, and do not foul up console. 

``` clojure
(format-num 27647234687347823658723657823) => "27647...[19]...57823"
```

It prints 5 first digits, 5 last digits and a number of digits between. By the way
it is configurable. You can pass a map with properties you want to override.

``` clojure
(format-num 27647234687347823658723657823 {:s 1 :e 1 :cnt false}) => "2...3"
```

Have a problem to read long number? Use the following method:

``` clojure
(number-name 16532561257523723757234781264) =>
"sixteen octillion five hundred thirty two septillion five hundred sixty one sextillion
two hundred fifty seven quintillion five hundred twenty three quadrillion seven hundred
twenty three trillion seven hundred fifty seven billion two hundred thirty four million
seven hundred eighty one thousand two hundred sixty four"
```

# Converters

### Roman numbers

``` clojure
(number->roman 19) => "XIX"
```

``` clojure
(roman->number "MCMLXXXIX") => 1989
```

# Factorial

### Stirling Approximation

Fast calculation approximate value of factorial

[TODO] implement

# Prime Numbers

Lazy sequence `primes` moved from `seqs.clj` to `primes.clj`

You can easily factorize your number to prime multipliers

``` clojure
(factorize 234) => [2 3 3 13]
```

To test whether number prime or not you have standard `prime?` predicate, works in O(sqrt(n)) time.

By the way, it is useful to have Euler's totient function

``` clojure
(totient 36) => 12
```

# Generators [NEW]

`(:use [numberto.generators])`

So, you want a random digit?

``` clojure
(rand-digit) => 3
```

A random number with 10 digits?

``` clojure
(rand-number 10) => 9026455947
```

# Math

Shuffle a number

```
(shuffle 123523) => 252331
```
