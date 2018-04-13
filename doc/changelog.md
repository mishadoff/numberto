# Changelog

### 0.0.4

* Number reader package. Handles singular and plurals in English, Ukrainian and Russian
* Solvers for "letter-digit" expressions
* Fixed nasty bug in `format-ratio` function
* Moved to Clojure 1.9.0

### 0.0.3

* Expressions package. Eval infix expression and build prefix.
* Solvers package. Solve puzzles "insert operations between numbers to get another number"
* Primes predicates `amicable?`, `perfect?`, `abundant?`.
* Radix converter
* Few math package functions improvements
* Generators: `rand-bigint` as standard `rand-int` but for big numbers
* Couple of new sequences `n-gonals`, `collatz`, `farey`, `fermat-numbers`, `powers-of`
* Printers: `format-ratio` to print rational numbers
* Irrational: calculate digits of `pi`, `e` and `sqrt(n)`
* Validator improved

### 0.0.2

* New namespace `printers` which allow to print long numbers
* Number to name conversion
* Roman numbers converter
* Separate namespace `primes`. Functions `factorize` and `totient`
* New namespace `generators`
* `shuffle` number
* Fast palindromes sequence 

### 0.0.1

Initial release.

* Converters to treat number like a sequence of digits.
* Common math operations like `sum`, `product`, `power`, `gcd`, etc.
* Factorial and *Improved* Factorial by factorization
* Lazy sequences: `primes`, `fibonacci`, `continued-fractions-sqroot`, `farey` and others
* Predicates `prime?`, `palindrome?`, `permutation?`
