package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.runtime.types.value.BooleanValue
import dev.vanadium.avo.runtime.types.value.FloatValue
import dev.vanadium.avo.runtime.types.value.IntegerValue
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.runtime.types.value.StringValue

sealed class LiteralNode(
    @Transient
    override val line: Int
) : ExpressionNode(line) {
    abstract fun runtimeValue(): RuntimeValue

    data class IntegerLiteral(
        @Transient
        override val line: Int,
        val value: Int
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return IntegerValue(value)
        }
        override fun toString(): String = "Integer Literal"
    }

    data class FloatLiteral(
        @Transient
        override val line: Int,
        val value: Double
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return FloatValue(value)
        }
        override fun toString(): String = "Float Literal"
    }

    data class StringLiteral(
        @Transient
        override val line: Int,
        val value: String
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return StringValue(value)
        }
        override fun toString(): String = "String Literal"
    }

    data class BooleanLiteral(
        @Transient
        override val line: Int,
        val value: Boolean
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return BooleanValue(value)
        }
        override fun toString(): String = "Boolean Literal"
    }
}