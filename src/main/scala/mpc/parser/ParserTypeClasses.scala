package org.treemage
package mpc.parser

import mpc.typeclasses.Monad

object ParserTypeClasses:
  given Monad[Parser] = new Monad[Parser]:
    override def flatMap[A, B](fa: Parser[A])(f: A => Parser[B]): Parser[B] =
      Parser(input =>
        fa.run(input) match
          case ParserResult.Success(remainingInput, currentValue) =>
            f(currentValue).run(remainingInput)
          case ParserResult.Fail(remainingInput, error) =>
            ParserResult.Fail(remainingInput, error)
      )

    override def pure[A](x: A): Parser[A] = Parser.succeed(x)
