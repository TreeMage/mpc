package org.treemage
package mpc.parser.extensions

import mpc.parser.ParserResult
import mpc.parser.Parser
import mpc.parser.ParserError

object SeqExtensions:
  extension [A, F[_] <: Seq[A]](self: Parser[F[A]])
    def nonEmpty: Parser[F[A]] = Parser(input =>
      self.run(input) match
        case success @ ParserResult.Success(remainingInput, currentValue)
            if currentValue.nonEmpty =>
          success
        case ParserResult.Success(remainingInput, currentValue) =>
          ParserResult.Fail(
            remainingInput,
            ParserError.UnexpectedEndOfInput(
              remainingInput.currentLocation,
              s"Expected at least one element in collection, but found zero."
            )
          )
        case fail @ ParserResult.Fail(_, _) => fail
    )
