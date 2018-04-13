# Getting Started

Lein dependency

```
[numberto "0.0.4"]
```

* [Converters](#converters)
* [Math](#math)
* [Factorial](#factorial)
* [Seqs](#seqs)
* [Primes](#primes)
* [Printers](#printers)
* [Generators](#generators)
* [Irrational](#irrational)
* [Expressions](#expressions)
* [Solvers](#solvers)
* [Number Reader](#number_reader)

### Converters

`(:use [numberto.converters])`

Converters allows you to treat number as sequence of digits and vice versa.

They can work with numbers

```clojure
(num->digits 12345) => (1 2 3 4 5)
```

or strings

```clojure
(map char->digit "12345") => (1 2 3 4 5)
```

To get back number view from list

```clojure
(digits->num [1 2 3 4 5]) => 12345N
```

or char sequence

```clojure
(map digit->char [1 2 3 4 5]) => (\1 \2 \3 \4 \5)
```

Handle conversion between different radix

```clojure
(radix-convert "100" 10 2) => "1100100"
(radix-convert "ff" 16 10) => "255"
```

Need a roman numbers?

```clojure
(number->roman 19) => "XIX"
```

```clojure
(roman->number "MCMLXXXIX") => 1989
```

### Math

`(:use [numberto.math])`

Math namespace provides functions to perform number manipulations

```clojure
(count-digits 123456789) => 9
(sum-of-digits 123456789) => 45
(sum-of-digits-recur 123456789) => 9
(reverse-num 123456789) => 987654321
(shift-left 123456789 3) => 456789123
(shift-right 123456789 3) => 789123456
(shuffle-num 123523) => 252331
```

Some math functions included as well, like exponentiation

```clojure
(power 10 3) => 1000
(power* 10 3) => 1000
```

`power*` uses exponatiation by squaring, and can greatly reduce calculation time for large powers

```clojure
(time (power 9 100000))
;; "Elapsed time: 8882.699887 msecs"
(time (power* 9 100000))
;; "Elapsed time: 123.425329 msecs"
```

and common ones

```clojure
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

```clojure
(! 100000) => ....very long number...
(count-digits (! 100000)) => 456574
```

There is also a factorial version `!!` optimized for big numbers.

```clojure
(time (! 100000))
;; "Elapsed time: 21052.309721 msecs"
(time (!! 100000))
;; "Elapsed time: 6877.630666 msecs"
```

### Seqs

`(:use [numberto.seqs])`

Lazy sequences are great. Always cut results before output.

```clojure
(take 10 naturals) => (1 2 3 4 5 6 7 8 9 10)
(take 10 squares) => (1 4 9 16 25 36 49 64 81 100)
(take 10 (powers-of 2)) => (1 2 4 8 16 32 64 128 256 512)
(take 10 triangles) => (1 3 6 10 15 21 28 36 45 55)
```

And your favorite

```clojure
(take 10 fibonacci) => (1 1 2 3 5 8 13 21 34 55)
```

Some are not very popular

```clojure
(take 10 (continued-fraction-sqroot 3)) => (1 1 2 1 2 1 2 1 2 1)
(take 10 (farey 5)) => ([0 1] [1 5] [1 4] [1 3] [2 5] [1 2] [3 5] [2 3] [3 4] [4 5])
(take 10 (collatz 5)) => [5 16 8 4 2 1]
```

Palindromic sequence. Instead of iterating all numbers and filter out
palindromic ones, we generate sorted palindromic sequence. Lazy.

For example 10000th palindrome

```clojure
(time (last (take 10000 (palindromes)))) 
;; "Elapsed time: 82.214392 msecs"
```

Naive approach much slower

```clojure
(time (last (take 10000 (filter palindrome? (range)))))
;; "Elapsed time: 8065.947083 msecs"
```

### Primes

Primes namespaces provide a bunch of functions related to prime numbers

```clojure
(take 10 (primes)) => (2 3 5 7 11 13 17 19 23 29)
```

You can easily factorize your number to prime multipliers

```clojure
(factorize 234) => [2 3 3 13]
```

To test whether number prime or not you have standard `prime?` predicate, works in O(sqrt(n)) time.

By the way, it is useful to have Euler's totient function

```clojure
(totient 36) => 12
```

### Printers

`(:use [numberto.printers])`

If you have a *really* big number, printing it to `System.out` not a good idea.
`format-num` allows you to capture important property of number, and do not foul up console. 

```clojure
(format-num 27647234687347823658723657823) => "27647...[19]...57823"
```

It prints 5 first digits, 5 last digits and a number of digits between. By the way
it is configurable. You can pass a map with properties you want to override.

```clojure
(format-num 27647234687347823658723657823 {:s 1 :e 1 :cnt false}) => "2...3"
```

Ratio numbers presented as `p/q` in clojure, but sometimes
needed to see digits after period. `(double 22/7)` can't handle this, use `format-ratio`

```clojure
(double 22/7) => 3.142857142857143
(format-ratio 22/7 30) => "3.14285714285714285714285714285"
```

### Generators

`(:use [numberto.generators])`

So, you want a random digit?

```clojure
(rand-digit) => 3
```

A random number with 10 digits?

```clojure
(rand-number 10) => 9026455947
```

A random number below n? (*rand-int not able to handle bigints*)

```clojure
(rand-bigint 12345678901234567890) => 5957548380330372271
```

### Irrational

`(:use [numberto.irational])`

What are digits of PI? `Math/PI` gives us only 15 digits.

```clojure
Math/PI => 3.141592653589793
(pi) => "3.141592653589793"
(pi :iterations 1000 :limit 50) =>
"3.1415926535897932384626433832795028841971693993751"
```

The same functionality available for `e` and `sqrt n`

```clojure
(e :iterations 100 :limit 50) =>
"2.7182818284590452353602874713526624977572470936999"

(sqrt 2 :iterations 100 :limit 50) =>
"1.4142135623730950488016887242096980785696718753769"
```

### Expressions

`(:use [numberto.expressions])`

Expressions package provides capability to evaluate infix
expression `eval-infix` and convert it to prefix lisp-style form `infix->prefix`

Let's give aliases to these operations

```clojure
(def e eval-infix)
(def p infix->prefix)
```

So, to evaluate simple math expressions feed it with string

```clojure
(e "1+2") => 3
```

or more complex

```clojure
(e "1+2*(3-4/2)") => 3
```

handle priorities

```clojure
(e "2+2*2") => 6
```

and left/right associativity

```clojure
(e "1024/2/2/2/2") => 64
(e "2^3^4") => 2417851639229258349412352N
```

Oh, what's this? Long numbers? Sure, ratios and floats supported as well

```clojure
(e "1/3") => 1/3
(e "1.1/0.9") => 1.2222222222222223
```

Unary operations

```clojure
(e "(-1)^100") => 1
```

functions and symbols

```clojure
(e "sin(e) + sqrt(pi)") => 2.183235141408425
```

vararg functions

```clojure
(e "sum(1,2,3,sum())/max(1,2)") => 3
```

You can also provide custom bindings for
unknown functions and symbols

```clojure
(e "factorial(n)/20"
   {:bindings
     {"factorial" #(reduce *' (range 1 (inc %)))
      "n" 10}})
=> 181440
```

Worth to mention that you can easily redefine existing
or define your own new unary, binary operations, functions
and symbols. Just add additional properties to `eval-infix`

```clojure
;; return current time in millis
(e "now()" {:bindings {"now" #(.getTime (java.util.Date.))}}) => some long number
;; override priorities
(e "1+2*3" {:binary-ops {"+" {:function + :priority 100}}}) => 9
```

`infix->prefix` has exactly the same functionality, but it builds prefix expression instead.

```clojure
(infix->prefix "(1+2)*3-(4/avg(3,5)-sum(1))")
=>
"(- (* (+ 1 2) 3) (- (/ 4 (avg 3 5)) (sum 1)))"
```

It can be useful if you googled some formula but bored to translate it manually to clojure.

For example, take the [Simpson's rule](http://en.wikipedia.org/wiki/Simpson%27s_rule)

![](http://upload.wikimedia.org/math/1/a/0/1a0fb4456375307fdde8ab85954d95be.png)

```clojure
(infix->prefix "(b-a)/6*(f(a)+4*f((a+b)/2)+f(b))")
=>
"(* (/ (- b a) 6) (+ (+ (f a) (* 4 (f (/ (+ a b) 2)))) (f b)))"
```

### Solvers

`(:use [numberto.solvers])`

Here is the puzzle:

> You have four numbers [3, 4, 5, 6].  
> You have four binary operations [+, -, *, /] and parentheses ()
>
> How to insert operations between numbers to get number 42?

Hah, that simple `3*4 + 5*6 = 42`

Ok, get `42`, but you forced to use one division `/`.

Not so obvious?

```clojure
(solve-insert-ops-num [3 4 5 6] 42) =>
([42N "3+45-6"] [42N "3/4*56"] [42N "3*4+5*6"])
```

If you use `solve-insert-ops` function it gives all possible values can be obtained by inserting operations between numbers.

```clojure
(solve-insert-ops [3 4 5 6]) => ;; long list
```

Default implementation uses 4 basic operations, no parenthesisand no restrictions. Instead, you can override options

to use parens, specify level

```clojure
(solve-insert-ops-num [3 4 5 6] 42 {:parens 1}) =>
([42N "3+45-6"] [42N "(3+45)-6"] [42N "3+(45-6)"] [42N "3/4*56"] [42N "(3/4)*56"] [42N "3/(4/56)"] [42N "3*4+5*6"] [42N "(3*4)+5*6"] [42N "3*4+(5*6)"])
```

limit some operations

```clojure
(solve-insert-ops-num [3 4 5 6] 42 {:rules [[:max "*" 1]]}) =>
([42N "3+45-6"] [42N "3/4*56"])
```

`:max`, `:min`, `:max-in-a-row`, `:min-in-a-row` options are supported.

Add new operations (supported by expressions package)

```clojure
(solve-insert-ops-num [3 4 5 6] 80
                      {:ops ["+" "-" "*" "/" "^"]
					   :rules [[:max "^" 1]]}) =>
([80N "3^4+5-6"])
```

Keep in mind, always limit time consuming operations (*like* `^`) as it building all permutations and you can wait your answer forever.

**Note:** Almost all number functions assuming bigint, and not optimized for areas where performace is critical.

### Number Reader

Do you need to read number in English?

```clojure
(number-name 1) => "one"
(number-name 23) => "twenty three"
(number-name 17596423) => "seventeen million five hundred ninety six thousand four hundred twenty three"
```

It can handle all numbers up to 10^52, including negatives. 
That's more than numbers of atoms in the world. More than enough.

```clojure
(number-name 16532561257523723757234781264) =>
"sixteen octillion five hundred thirty two septillion five hundred sixty one sextillion
two hundred fifty seven quintillion five hundred twenty three quadrillion seven hundred
twenty three trillion seven hundred fifty seven billion two hundred thirty four million
seven hundred eighty one thousand two hundred sixty four"
```

This function is also aware about singular and plural forms. 
Use simple DSL to define plural forms and you ready to go.

Use plural ending in parens.

```clojure
(def bird (->english-word "bird(s)"))

(number-name 1 :word bird) => "one bird"
(number-name 2 :word bird) => "two birds"
(number-name 123 :word bird) => "one hundred twenty three birds"
```

If the word has no plural forms, ok

```clojure
(def sheep (->english-word "sheep"))

(number-name 1 :word sheep) => "one sheep"
(number-name 2 :word sheep) => "two sheep"
```

If the word has two distinct words for singular and plural, use pipes

```clojure
(def mouse (->english-word "mouse|mice"))

(number-name 1 :word mouse) => "one mouse"
(number-name 2 :word mouse) => "two mice"
```

The challenge was to support Ukrainian and Russian languages. 

Both of those languages have much complex grammar rules for plurals. 
Also each word can be in one of three forms (male, female, it), which also affects counting word.
DSL for cyrillic words definition should support more transformations and forms. Inspect function doc for more details.

Ukrainian

```clojure
(def apple (->cyrillic-word "яблук(_|о|а)!i"))

(number-name 1 :lang :ukr :word apple) => "одне яблуко"
(number-name 2 :lang :ukr :word apple) => "два яблука"
(number-name 28 :lang :ukr :word apple) => "двадцять вісім яблук"
(number-name 31 :lang :ukr :word apple) => "тридцять одне яблуко"
```

Russian

```clojure
(def bottle (->cyrillic-word "бутыл(ок|ка|ки)!f"))

(number-name 1 :lang :ru :word bottle) => "одна бутылка"
(number-name 2 :lang :ru :word bottle) => "две бутылки"
(number-name 17 :lang :ru :word bottle) => "семнадцать бутылок"
(number-name 31 :lang :ru :word bottle) => "тридцать одна бутылка"
``` 