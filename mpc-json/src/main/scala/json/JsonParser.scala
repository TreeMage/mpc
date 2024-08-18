package org.treemage
package json

import mpc.parser.Parser
import mpc.parser.ParserOps.*
import mpc.parser.ParserTypeClasses.given
import mpc.parser.extensions.StringExtensions.*
import mpc.typeclasses.Applicative.{*>, <*, <*>}
import mpc.typeclasses.Functor
import mpc.typeclasses.Functor.*

import scala.language.postfixOps

object JsonParser:
  private def ws: Parser[String] = Parser.range(_.isWhitespace)
  private def sepIgnoringWhiteSpace(sep: Char): Parser[String] =
    ws *> Parser.char(sep) *> ws
  private def jsonInt: Parser[JsonValue.JsonInt] =
    Parser
      .regex("""[+\-]?\d+""".r)
      .nonEmpty
      .map(_.toInt)
      .map(JsonValue.JsonInt.apply)

  private def jsonFloat: Parser[JsonValue.JsonFloat] =
    Parser
      .regex("""[+-]?(\d*)\.\d+""".r)
      .nonEmpty
      .map(_.toFloat)
      .map(JsonValue.JsonFloat.apply)

  private def jsonBoolean: Parser[JsonValue.JsonBoolean] =
    (Parser.text("true") | Parser.text("false")).map {
      case "true"  => JsonValue.JsonBoolean(true)
      case "false" => JsonValue.JsonBoolean(false)
      case other =>
        throw new RuntimeException(
          s"Failed to parse boolean from '$other'. This might be a bug in the parser."
        )
    }

  private def jsonNull: Parser[JsonValue.JsonNull.type] =
    Parser.text("null").as(JsonValue.JsonNull)

  private def jsonString: Parser[JsonValue.JsonString] =
    (Parser.char('\"') *> Parser.range(_ != '\"') <* Parser.char('\"'))
      .map(JsonValue.JsonString.apply)

  private def block[A](open: Char, close: Char)(content: Parser[A]): Parser[A] =
    ws *> Parser.char(open) *> ws *> content <* ws <* Parser.char(close) <* ws

  private lazy val jsonArray: Parser[JsonValue.JsonArray] =
    block('[', ']')(jsonValue.seperatedBy(sepIgnoringWhiteSpace(',')))
      .map(JsonValue.JsonArray.apply)

  private lazy val keyValuePair: Parser[(JsonValue.JsonString, JsonValue)] =
    (ws *> jsonString <* ws <* Parser.char(
      ':'
    ) <* ws) <*> (ws *> jsonValue <* ws)

  private lazy val jsonObject: Parser[JsonValue.JsonObject] =
    block('{', '}')(keyValuePair.seperatedBy(sepIgnoringWhiteSpace(','))).map(
      entries =>
        JsonValue.JsonObject(Map.from(entries.map {
          case JsonValue.JsonString(key) -> value => key -> value
        }))
    )

  private lazy val jsonValue: Parser[JsonValue] =
    jsonNull | jsonFloat | jsonInt | jsonBoolean | jsonString | jsonArray | jsonObject

  lazy val jsonParser: Parser[JsonValue.JsonObject] = jsonObject
