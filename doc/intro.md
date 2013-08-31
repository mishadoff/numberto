# Getting Started

Lein dependency

```
[numberto "0.0.1"]
```

### Converters

`(:use [numberto.converters])`

Converters allows you to treat number as sequence of digits and vice versa.

They can work with numbers

``` clojure
(num->digits 12345) => (1 2 3 4 5)
```

or strings

``` clojure
(map char->digit "12345") => (1 2 3 4 5)
```

To get back number view from list

``` clojure
(digits->num [1 2 3 4 5]) => 12345N
```

or char sequence

``` clojure
(map digit->char [1 2 3 4 5]) => (\1 \2 \3 \4 \5)
```

Need a roman numbers?

``` clojure
(number->roman 19) => "XIX"
```

``` clojure
(roman->number "MCMLXXXIX") => 1989
```

### Math

`(:use [numberto.math])`

Math namespace provides functions to perform number manipulations

``` clojure
(count-digits 123456789) => 9
(sum-of-digits 123456789) => 45
(reverse-num 123456789) => 987654321
(shift-left 123456789 3) => 456789123
(shift-right 123456789 3) => 789123456
(shuffle 123523) => 252331
```

Some math functions included as well, like exponentiation

``` clojure
(power 10 3) => 1000
(power* 10 3) => 1000
```

`power*` uses exponatiation by squaring, and can greatly reduce calculation time for large powers

``` clojure
(time (power 9 100000))
;; "Elapsed time: 8882.699887 msecs"
(time (power* 9 100000))
;; "Elapsed time: 123.425329 msecs"
```

and common ones

``` clojure
(square 10) => 100
(sqroot E) => 1.6487212707001282
(abs -3) => 3
(sum [1 2 3 4 5]) => 15
(product [1 2 3 4 5]) => 120
(avg [1 2 3 4 5]) => 3.0
(gcd 100 30) => 10
(lcm 100 30) => 300
```

### Factorial

`(:use [numberto.factorial])`

What about factorial? 

``` clojure
(! 100000) => ....very long number...
(count-digits (! 100000)) => 456574
```

There is also a factorial version `!!` optimized for big numbers.

``` clojure
(time (! 100000))
;; "Elapsed time: 21052.309721 msecs"
(time (!! 100000))
;; "Elapsed time: 6877.630666 msecs"
```

### Seqs

Lazy sequences are good composable objects. Always cut results before output.

``` clojure
(take 10 naturals) => (1 2 3 4 5 6 7 8 9 10)
(take 10 squares) => (1 4 9 16 25 36 49 64 81 100)
(take 10 powers-of-two) => (1 2 4 8 16 32 64 128 256 512)
(take 10 triangles) => (1 3 6 10 15 21 28 36 45 55)
```

And your favorite

``` clojure
(take 10 fibonacci) => (1 1 2 3 5 8 13 21 34 55)
```

Some are not very popular

``` clojure
(take 10 (continued-fraction-sqroot 3)) => (1 1 2 1 2 1 2 1 2 1)
(take 10 (farey 5)) => ([0 1] [1 5] [1 4] [1 3] [2 5] [1 2] [3 5] [2 3] [3 4] [4 5])
```

Palindromic sequence. Instead of iterating all numbers and filter out
palindromic ones, we generate sorted palindromic sequence. Lazy.

For example 10000th palindrome

``` clojure
(time (last (take 10000 (palindromes)))) 
;; "Elapsed time: 82.214392 msecs"
```

Naive approach much slower

``` clojure
(time (last (take 10000 (filter palindrome? (range)))))
;; "Elapsed time: 8065.947083 msecs"
```

### Primes

Primes namespaces provide a bunch of functions related to prime numbers

``` clojure
(take 10 (primes)) => (2 3 5 7 11 13 17 19 23 29)
```

You can easily factorize your number to prime multipliers

``` clojure
(factorize 234) => [2 3 3 13]
```

To test whether number prime or not you have standard `prime?` predicate, works in O(sqrt(n)) time.

By the way, it is useful to have Euler's totient function

``` clojure
(totient 36) => 12
```

### Printers

`(:use [numberto.printers])`

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

# Generators

`(:use [numberto.generators])`

So, you want a random digit?

``` clojure
(rand-digit) => 3
```

A random number with 10 digits?

``` clojure
(rand-number 10) => 9026455947
```


**Note:** Almost all number functions assuming bigint, and not optimized for areas where performace is critical.
