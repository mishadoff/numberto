Features to be added

* Primes
  - `sum-of-proper-divisors`
  - `amicable?`
  - `perfect?`
  - `abundant?`
* Converters
  - Improved `roman->number`
  - Function `radix-convert` between different bases. Only integers
* Math
  - `sum-of-digits-recur`
  - `power*` avoid potential SO
  - `log`, `log2` functions for bignums
  - `div?`
* Generators
  - `rand-bigint`
* Sequences
  - `powers-of-two` generalized to `powers-of`
  - `pentagonals`
  - `hexagonals`
  - `collatz`
  - `farey`
  - `fermat-numbers`
* Printers
  - `format-ratio` ;; TODOS
* Irrational
  - `e`, `pi`, `sqrt n` calculations
* Validator improved
* Expressions
  - `eval-infix` to eval math expressions
    - Simple expressions "1+2"
	- Math operations support "2^10 - 3 * (- 10 - 1/5)"
	- Works for bignums "100^100"
	- Works for floats "1.123 / 3.621"
	- Unary operations support "-1 * ++3"
	- TODO Custom defined operations 
	- Support functions and symbols "cos(0) + sin(pi)"
	- Custom defined functions and symbols
	- Vararg functions "sum(1,2,3) - avg(10,29) - avg(1,2,3,4,5)"
	- ?
  - `infix->prefix` to build prefix notation for infix expression 
* Solvers
