package org.treemage
package mpc.parser

import ParserError.{UnexpectedEndOfInput, UnexpectedInput}
import mpc.typeclasses.Functor.*
import mpc.typeclasses.Monad.*
import ParserTypeClasses.given
import ParserOps.*
import mpc.typeclasses.Traversable.*
import mpc.util.ListTypeClasses.given

import scala.annotation.tailrec

case class Parser[+A](run: ParserInput => ParserResult[A])

object Parser:
  def succeed[A](value: A): Parser[A] = Parser(ParserResult.Success(_, value))
  def fail(error: ParserError): Parser[Nothing] = Parser(
    ParserResult.Fail(_, error)
  )

  def char(char: Char): Parser[Char] = Parser(input =>
    input.nextChar match
      case Some(value) if value == char =>
        ParserResult.Success(input.advanceBy(1).get, value)
      case Some(value) =>
        ParserResult.Fail(
          input,
          ParserError.UnexpectedInput(
            input.currentLocation,
            s"Expected '$char' but found '$value'."
          )
        )
      case None =>
        ParserResult.Fail(
          remainingInput = input,
          error = UnexpectedEndOfInput(
            input.currentLocation,
            s"Expected '$char' but found end of file"
          )
        )
  )

  def text(txt: String): Parser[String] = txt
    .map(Parser.char(_))
    .toList
    .sequence
    .map(_.mkString)

  def range(predicate: Char => Boolean): Parser[String] = Parser(input =>
    @tailrec
    def go(
        acc: List[Char],
        currentInput: ParserInput
    ): (List[Char], ParserInput) =
      currentInput.nextChars(1) match
        case Some(value) =>
          if (predicate(value.head))
            go(value.head +: acc, currentInput.advanceBy(1).get)
          else
            (acc, currentInput)
        case None => (acc, currentInput)

    val (acc, currentInput) = go(List.empty, input)
    ParserResult.Success(currentInput, acc.reverse.mkString)
  )

  // TODO: Make this tail-recursive
  def sepBy[A](sep: Parser[Any])(value: Parser[A]): Parser[List[A]] =
    for
      first    <- value.option
      firstSep <- sep.option
      tail <- firstSep match
        case None    => Parser.succeed(List.empty)
        case Some(_) => sepBy(sep)(value)
    yield first match
      case Some(head) => head +: tail
      case None       => List.empty
