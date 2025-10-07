package org.vanadium.avo.syntax.ast

enum class BinaryOperationType {
    PLUS,
    MINUS,
    MULTIPLY,
    DIVIDE,
    POWER,
    MODULUS,
}

enum class UnaryOperationType {
    INCREMENT,
    DECREMENT,
    NEGATE,
}