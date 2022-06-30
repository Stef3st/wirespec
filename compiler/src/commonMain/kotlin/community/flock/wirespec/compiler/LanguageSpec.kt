package community.flock.wirespec.compiler

import community.flock.wirespec.compiler.tokenize.Colon
import community.flock.wirespec.compiler.tokenize.Comma
import community.flock.wirespec.compiler.tokenize.Identifier
import community.flock.wirespec.compiler.tokenize.LeftCurly
import community.flock.wirespec.compiler.tokenize.NewLine
import community.flock.wirespec.compiler.tokenize.RightCurly
import community.flock.wirespec.compiler.tokenize.Token
import community.flock.wirespec.compiler.tokenize.WhiteSpaceExceptNewLine
import community.flock.wirespec.compiler.tokenize.WsBoolean
import community.flock.wirespec.compiler.tokenize.WsInteger
import community.flock.wirespec.compiler.tokenize.WsString
import community.flock.wirespec.compiler.tokenize.WsTypeDef

interface LanguageSpec {
    val matchers: List<Pair<Regex, Token.Type>>
}

object WireSpec : LanguageSpec {
    @Suppress("RegExpRedundantEscape")
    override val matchers = listOf(
        Regex("^type") to WsTypeDef,
        Regex("^[^\\S\\r\\n]+") to WhiteSpaceExceptNewLine,
        Regex("^[\\r\\n]") to NewLine,
        Regex("^\\{") to LeftCurly,
        Regex("^:") to Colon,
        Regex("^,") to Comma,
        Regex("^String") to WsString,
        Regex("^Integer") to WsInteger,
        Regex("^Boolean") to WsBoolean,
        Regex("^\\}") to RightCurly, // "^\\}" Redundant Escape in regex is needed for js compatibility
        Regex("^[a-zA-Z]+") to Identifier,
    )
}
