package org.treemage
package mpc.parser

case class Location(row: Int, column: Int)

enum ParserError:
  case UnexpectedEndOfInput(location: Location, message: String)
  case UnexpectedInput(location: Location, message: String)

enum ParserResult[+A]:
  case Success(remainingInput: ParserInput, currentValue: A)
  case Fail(remainingInput: ParserInput, error: ParserError)
