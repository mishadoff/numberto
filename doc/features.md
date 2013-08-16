Features to be added

# Converters

### Roman numbers

``` clojure
(number->roman 19) => "XIX"
```

``` clojure
(roman->number "MCMLXXXIX") => 1989
```

### Binary systems (more than 36)

[TODO]

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

Some additional methods like `mersenne-primes` or `fermat-primes`

To test whether number prime or not you have standard `prime?` predicate,
works in O(sqrt(n)) time. Another primality tests are [TODO]

# Generators

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
