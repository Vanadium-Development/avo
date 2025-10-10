package org.vanadium.avo.runtime

import org.vanadium.avo.exception.SyntaxException

sealed class RuntimeValue {
    abstract fun plus(other: RuntimeValue): RuntimeValue
    abstract fun minus(other: RuntimeValue): RuntimeValue
    abstract fun times(other: RuntimeValue): RuntimeValue

    data class IntegerValue(val value: Int) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value + other.value)
            is FloatValue -> FloatValue(value + other.value)
            is StringValue -> StringValue(value.toString() + other.value)
            else -> throw SyntaxException("Invalid operands for addition: $this and $other")
        }

        override fun minus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value - other.value)
            is FloatValue -> FloatValue(value - other.value)
            else -> throw SyntaxException("Invalid operands for subtraction: $this and $other")
        }

        override fun times(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value * other.value)
            is FloatValue -> FloatValue(value *  other.value)
            else -> throw SyntaxException("Invalid operands for multiplication: $this and $other")
        }
    }

    data class FloatValue(val value: Double) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value + other.value.toDouble())
            is FloatValue -> FloatValue(value + other.value)
            is StringValue -> StringValue(value.toString() + other.value)
            else -> throw SyntaxException("Invalid operands for addition: $this and $other")
        }

        override fun minus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value - other.value)
            is FloatValue -> FloatValue(value - other.value)
            else -> throw SyntaxException("Invalid operands for subtraction: $this and $other")
        }

        override fun times(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value * other.value)
            is FloatValue -> FloatValue(value + other.value)
            else -> throw SyntaxException("Invalid operands for multiplication: $this and $other")
        }
    }

    data class StringValue(val value: String) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> StringValue(value + other.value.toString())
            is FloatValue -> StringValue(value + other.value.toString())
            is StringValue -> StringValue(value + other.value)
            else -> throw SyntaxException("Invalid operands for addition: $this and $other")
        }

        override fun minus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for subtraction: $this and $other")
        }

        override fun times(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for addition: $this and $other")
        }
    }

    data class BooleanValue(val value: Boolean) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for addition: $this and $other")
        }

        override fun minus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for subtraction: $this and $other")
        }

        override fun times(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for multiplication: $this and $other")
        }
    }
}