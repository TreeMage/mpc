package org.treemage
package mpc

import mpc.parser.ParserTypeClasses.given
import mpc.parser.extensions.StringExtensions.nonEmpty
import mpc.parser.{Parser, ParserInput, ParserResult}
import mpc.typeclasses.Applicative.{*>, <*}
import mpc.typeclasses.Functor.*
import mpc.typeclasses.{Applicative, Functor, Traversable}

import org.treemage.json.JsonParser

@main
def main: Unit =
  val text =
    """
      |{
      |  "it": {
      |     "works": [1, {"with": ["nested",[true, "stuff", null]]}],
      |  },
      |  "yeah": null
      |}""".stripMargin
  val parser =
    for parsed <- JsonParser.jsonParser
    yield parsed
  parser.run(ParserInput.make(text)) match
    case ParserResult.Success(remainingInput, currentValue) =>
      println(currentValue)
    case ParserResult.Fail(remainingInput, error) => println(error)
