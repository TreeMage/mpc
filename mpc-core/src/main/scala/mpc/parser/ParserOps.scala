package org.treemage
package mpc.parser

import mpc.parser.ParserTypeClasses.given
import mpc.typeclasses.Functor.*
import mpc.typeclasses.Monad.*

import org.treemage.mpc.typeclasses.Applicative.*>

import scala.annotation.{showAsInfix, targetName}

object ParserOps:
  extension [A](self: Parser[A])
    def option: Parser[Option[A]] = Parser(input =>
      self.run(input) match
        case ParserResult.Success(remainingInput, currentValue) =>
          ParserResult.Success(remainingInput, Some(currentValue))
        case ParserResult.Fail(remainingInput, error) =>
          ParserResult.Success(input, None)
    )

    def orElse[B](other: => Parser[B]): Parser[A | B] =
      for
        first <- self.option
        second <- first match
          case None        => other
          case Some(value) => Parser.succeed(value)
      yield second

    @targetName("infixOr")
    def |[B](other: => Parser[B]): Parser[A | B] = self.orElse(other)

    def seperatedBy(sep: => Parser[Any]): Parser[List[A]] =
      Parser.sepBy(sep)(self)

    def as[B](value: => B): Parser[B] =
      self.map(_ => value)
