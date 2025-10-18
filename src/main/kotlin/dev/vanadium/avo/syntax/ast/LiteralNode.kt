package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.runtime.interpreter.types.value.BooleanValue
import dev.vanadium.avo.runtime.interpreter.types.value.FloatValue
import dev.vanadium.avo.runtime.interpreter.types.value.IntegerValue
import dev.vanadium.avo.runtime.interpreter.types.value.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.types.value.StringValue

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
    }

    data class FloatLiteral(
        @Transient
        override val line: Int,
        val value: Double
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return FloatValue(value)
        }
    }

    data class StringLiteral(
        @Transient
        override val line: Int,
        val value: String
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return StringValue(value)
        }
    }

    data class BooleanLiteral(
        @Transient
        override val line: Int,
        val value: Boolean
    ) : LiteralNode(line) {
        override fun runtimeValue(): RuntimeValue {
            return BooleanValue(value)
        }
    }
}