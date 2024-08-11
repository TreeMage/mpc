package org.treemage
package json

import mpc.parser.Parser
import mpc.parser.extensions.StringExtensions.*
import mpc.parser.ParserTypeClasses.given
import mpc.typeclasses.Functor
import mpc.typeclasses.Functor.*

import org.treemage.mpc.parser.ParserOps.*
import org.treemage.mpc.typeclasses.Applicative.{*>, <*, <*>}

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

  private def jsonFloat: Parser[JsonValue.JsonFloat] = Parser
    .regex("""[+-]?(\d*)\.\d+""".r)
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

  private lazy val jsonArray = ws *> Parser.char('[') *> ws *> jsonValue
    .seperatedBy(sepIgnoringWhiteSpace(','))
    .map(JsonValue.JsonArray.apply) <* ws <* Parser.char(']') <* ws

  private lazy val keyValuePair: Parser[(JsonValue.JsonString, JsonValue)] =
    (ws *> jsonString <* ws <* Parser.char(
      ':'
    ) <* ws) <*> (ws *> jsonValue <* ws)

  private lazy val jsonObject: Parser[JsonValue.JsonObject] =
    (ws *> Parser.char('{') *> ws *> keyValuePair.seperatedBy(
      sepIgnoringWhiteSpace(',')
    ) <* ws <* Parser.char('}') <* ws).map(entries =>
      JsonValue.JsonObject(Map.from(entries.map {
        case JsonValue.JsonString(key) -> value => key -> value
      }))
    )

  private lazy val jsonValue: Parser[JsonValue] =
    jsonNull | jsonFloat | jsonInt | jsonBoolean | jsonString | jsonArray | jsonObject

  lazy val jsonParser: Parser[JsonValue.JsonObject] = jsonObject
