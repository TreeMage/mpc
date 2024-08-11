package org.treemage
package json

enum JsonValue:
  case JsonNull
  case JsonInt(value: Int)
  case JsonFloat(value: Float)
  case JsonString(value: String)
  case JsonBoolean(value: Boolean)
  case JsonArray(values: List[JsonValue])
  case JsonObject(values: Map[String, JsonValue])
