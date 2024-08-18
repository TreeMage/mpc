## Monadic Parser Combinators
This project is a toy implementation of monadic parser combinators. 
I built this because I was curious about how this paradigm of building parsers works, so beware if you intend to use
any of the code in this repository :)
### What are monadic parser combinators?
The core idea behind monadic parser combinators is a "bottom-up" where we first define simple building blocks, for
instance, a parser that always returns a given value, a parser that consumes exactly one character or a parser that 
consumes input as long as a predicate is met. <br/>
Afterward, we define operators - also known as combinators - that combine these building blocks into more complex 
parsers. A simple combinator is `orElse` which takes two parsers, runs the first one and, depending on whether it 
succeeded, returns its result or runs the second one. A more complex one is `sepBy` which takes a parser that parses
values of type `A` as well as a parser that is able to parser a separator string (e.g. ',') and returns a `List[A]`.
<br/>
A powerful way of enabling easy combination of parsers are Monads. A Monad can be though of additional context that is
associated with a computation. This is necessary because combining two parsers requires to compose functions that have
signatures like `A => Parser[B]` and `B => Parser[C]` into a single function `A => Parser[C]`. A Monad provides a way of
achieving this composition while also providing a lot of other helpful functionality along the way.
### Basic building blocks available in `mpc-core`
These serve as the foundation to build more complex parsers from.

| Name      | Description                                                                  |
|-----------|------------------------------------------------------------------------------|
| `succeed` | Creates a parser that always succeeds with a given value.                    |
| `fail`    | Creates a parser that always fails with a given error message.               |
| `char`    | Parses a single character if it matches the given character.                 |
| `text`    | Parses a sequence of characters if it matches the given string.              |
| `regex`   | Parses a sequence of characters that match the given regular expression.     |
| `range`   | Parses a sequence of characters as long as they satisfy the given predicate. |


### Combinators available in `mpc-core`
These allow to combine the basic building blocks into more complex parsers.

| Name          | Description                                                                        | Signature                                               |
|---------------|------------------------------------------------------------------------------------|---------------------------------------------------------|
| `map`         | Transforms the result of a parser using a given function.                          | `def map[B](f: A => B): Parser[B]`                      |
| `flatMap`     | Chains parsers by using the result of one parser to determine the next.            | `def flatMap[B](f: A => Parser[B]): Parser[B]`          |
| `option`      | Converts a parser to one that always succeeds, wrapping the result in an `Option`. | `def option: Parser[Option[A]]`                         |
| `orElse`      | Tries the first parser, and if it fails, tries the second parser.                  | `def orElse[B](other: => Parser[B]): Parser[A\| B]`     |
| `\|`          | Infix version of `orElse`.                                                         | `def \|[B](other: => Parser[B]): Parser[A\| B]`         |
| `seperatedBy` | Parses a list of elements separated by a given separator parser.                   | `def seperatedBy(sep: => Parser[Any]): Parser[List[A]]` |
| `as`          | Transforms the result of a parser to a given value.                                | `def as[B](value: => B): Parser[B]`                     |