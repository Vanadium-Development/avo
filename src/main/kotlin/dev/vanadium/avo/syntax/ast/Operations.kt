package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.TokenType

enum class BinaryOperationType {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    MODULUS;

    companion object {
        fun fromTokenType(type: TokenType) = when (type) {
            TokenType.PLUS -> PLUS
            TokenType.MINUS -> MINUS
            TokenType.ASTERISK -> MULTIPLY
            TokenType.SLASH -> DIVIDE
            TokenType.PERCENT -> MODULUS
            TokenType.CARET -> POWER
            else -> null
        }
    }

}

enum class UnaryOperationType {
    INCREMENT,
    DECREMENT,
    NEGATE,
}