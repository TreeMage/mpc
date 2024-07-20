package org.treemage
package mpc.parser.extensions

import mpc.parser.ParserResult
import mpc.parser.Parser
import mpc.parser.ParserError

object StringExtensions:
  extension (self: Parser[String])
    def nonEmpty: Parser[String] = Parser(input =>
      self.run(input) match
        case ParserResult.Success(remainingInput, currentValue)
            if currentValue.isEmpty =>
          ParserResult.Fail(
            input,
            ParserError.UnexpectedInput(
              input.currentLocation,
              "Expected range to be non-empty but matched zero characters."
            )
          )
        case success @ ParserResult.Success(_, _) => success
        case fail @ ParserResult.Fail(_, _)       => fail
    )
