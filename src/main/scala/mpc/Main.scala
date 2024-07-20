package org.treemage
package mpc

import mpc.parser.ParserTypeClasses.given
import mpc.parser.extensions.StringExtensions.nonEmpty
import mpc.parser.{Parser, ParserInput, ParserResult}
import mpc.typeclasses.Applicative.{*>, <*}
import mpc.typeclasses.Functor.*
import mpc.typeclasses.{Applicative, Functor}

@main
def main: Unit =
  val text =
    ""
  val ws         = Parser.range(_.isWhitespace)
  val uint       = Parser.range(_.isDigit).nonEmpty.map(_.toInt)
  val sep        = ws *> Parser.text(",") <* ws
  val listOfUInt = ws *> Parser.sepBy(sep)(uint)

  val parser =
    for parsed <- listOfUInt
    yield parsed
  parser.run(ParserInput.make(text)) match
    case ParserResult.Success(remainingInput, currentValue) =>
      println(currentValue)
    case ParserResult.Fail(remainingInput, error) => println(error)
