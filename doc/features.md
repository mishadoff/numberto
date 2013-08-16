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
