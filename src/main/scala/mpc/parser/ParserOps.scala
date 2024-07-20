package org.treemage
package mpc.parser

import ParserTypeClasses.given
import org.treemage.mpc.typeclasses.Functor.*
import org.treemage.mpc.typeclasses.Monad.*

object ParserOps:
  extension [A](self: Parser[A])
    def option: Parser[Option[A]] = Parser(input =>
      self.run(input) match
        case ParserResult.Success(remainingInput, currentValue) =>
          ParserResult.Success(remainingInput, Some(currentValue))
        case ParserResult.Fail(remainingInput, error) =>
          ParserResult.Success(input, None)
    )

    def orElse[B](other: Parser[B]): Parser[A | B] =
      for
        first <- self.option
        second <- first match
          case None        => other
          case Some(value) => Parser.succeed(value)
      yield second
