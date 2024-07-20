package org.treemage
package mpc.parser

import ParserError.{UnexpectedEndOfInput, UnexpectedInput}
import org.treemage.mpc.typeclasses.Functor.*
import org.treemage.mpc.typeclasses.Monad.*
import ParserTypeClasses.given
import ParserOps.*

import scala.annotation.tailrec

case class Parser[+A](run: ParserInput => ParserResult[A])

object Parser:
  def succeed[A](value: A): Parser[A] = Parser(ParserResult.Success(_, value))
  def fail(error: ParserError): Parser[Nothing] = Parser(
    ParserResult.Fail(_, error)
  )

  def text(txt: String): Parser[String] = Parser(input =>
    input.nextChars(txt.length) match
      case Some(value) =>
        if (value == txt)
          ParserResult.Success(input.advanceBy(txt.length).get, value)
        else
          ParserResult.Fail(
            input,
            ParserError.UnexpectedInput(
              input.currentLocation,
              s"Expected '$txt' but found '$value'."
            )
          )
      case None =>
        ParserResult.Fail(
          remainingInput = input,
          error = UnexpectedEndOfInput(
            input.currentLocation,
            s"Expected '$txt' but found only ${input.remainingCharacters} characters."
          )
        )
  )

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
