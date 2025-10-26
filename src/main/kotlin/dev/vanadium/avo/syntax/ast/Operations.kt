package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.TokenType

enum class BinaryOperationType {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    MODULUS,
    GREATER_THAN,
    LESS_THAN,
    GREATER_EQUAL,
    LESS_EQUAL,
    AND,
    OR,
    EQUALS;

    companion object {
        fun additiveFromTokenType(type: TokenType) = when (type) {
            TokenType.PLUS             -> PLUS
            TokenType.MINUS            -> MINUS
            TokenType.LESS_THAN        -> LESS_THAN
            TokenType.GREATER_THAN     -> GREATER_THAN
            TokenType.GREATER_EQUAL    -> GREATER_EQUAL
            TokenType.LESS_EQUAL       -> LESS_EQUAL
            TokenType.DOUBLE_EQUALS    -> EQUALS
            TokenType.DOUBLE_AMPERSAND -> AND
            TokenType.DOUBLE_BAR       -> OR
            else                       -> null
        }

        fun multiplicativeFromTokenType(type: TokenType) = when (type) {
            TokenType.ASTERISK -> MULTIPLY
            TokenType.SLASH    -> DIVIDE
            TokenType.PERCENT  -> MODULUS
            TokenType.CARET    -> POWER
            else               -> null
        }
    }

}

enum class UnaryOperationType {
    INCREMENT,
    DECREMENT,
    NEGATE,
}