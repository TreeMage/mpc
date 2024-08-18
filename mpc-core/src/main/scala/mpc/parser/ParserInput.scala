package org.treemage
package mpc.parser

case class ParserInput(input: String, currentChar: Int)

object ParserInput:
  def make(input: String): ParserInput =
    ParserInput(input, 0)

  extension (self: ParserInput)
    private def hasCapacityRemaining(length: Int): Boolean =
      self.remainingCharacters >= length

    def remainingCharacters: Int = self.input.length - self.currentChar
    def remainingText: String =
      self.input.slice(self.currentChar, self.input.length)
    def advanceBy(length: Int): Option[ParserInput] = Option.when(
      self.hasCapacityRemaining(length)
    )(self.copy(currentChar = self.currentChar + length))
    def nextChar: Option[Char] = Option.when(
      self.hasCapacityRemaining(1)
    )(self.input.charAt(self.currentChar))
    def nextChars(length: Int): Option[String] = Option.when(
      self.hasCapacityRemaining(length)
    )(self.input.slice(self.currentChar, self.currentChar + length))
    def currentLocation: Location =
      val lines = self.input.split("\n", -1)
      val cumulativeCharactersPerLine = lines
        .map(_.length)
        .foldLeft(List.empty[Int]) { case (acc, cur) =>
          (acc.sum + cur) +: acc
        }
        .reverse
      val (charactersUpToTheLine, lineIndex) =
        cumulativeCharactersPerLine.zipWithIndex
          .find { case (charactersUpToThisLine, index) =>
            charactersUpToThisLine > self.currentChar
          }
          .getOrElse(
            (
              self.input.length - lines.reverse.tail.map(_.length).sum,
              lines.length - 1
            )
          )
      val colIndex = self.currentChar - charactersUpToTheLine
      Location(lineIndex, colIndex)
