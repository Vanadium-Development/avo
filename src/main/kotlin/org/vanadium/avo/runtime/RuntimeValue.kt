package org.vanadium.avo.runtime

import org.vanadium.avo.exception.SyntaxException
import org.vanadium.avo.types.DataType
import kotlin.math.pow

sealed class RuntimeValue {
    abstract fun plus(other: RuntimeValue): RuntimeValue
    abstract fun minus(other: RuntimeValue): RuntimeValue
    abstract fun times(other: RuntimeValue): RuntimeValue
    abstract fun divide(other: RuntimeValue): RuntimeValue
    abstract fun modulo(other: RuntimeValue): RuntimeValue
    abstract fun pow(other: RuntimeValue): RuntimeValue

    abstract fun name(): String

    fun dataType() = when (this) {
        is IntegerValue -> DataType.IntegerType
        is FloatValue -> DataType.FloatType
        is StringValue -> DataType.StringType
        is BooleanValue -> DataType.BooleanType
        is VoidValue -> DataType.VoidType
        is LambdaValue -> DataType.LambdaType(this.function.signature.map { it.type }, this.function.returnType)
    }

    data class IntegerValue(val value: Int) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value + other.value)
            is FloatValue -> FloatValue(value + other.value)
            is StringValue -> StringValue(value.toString() + other.value)
            else -> throw SyntaxException("Invalid operands for addition: ${this.name()} and ${other.name()}")
        }

        override fun minus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value - other.value)
            is FloatValue -> FloatValue(value - other.value)
            else -> throw SyntaxException("Invalid operands for subtraction: ${this.name()} and ${other.name()}")
        }

        override fun times(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value * other.value)
            is FloatValue -> FloatValue(value * other.value)
            is StringValue -> StringValue(other.value.repeat(value))
            else -> throw SyntaxException("Invalid operands for multiplication: ${this.name()} and ${other.name()}")
        }

        override fun divide(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value / other.value)
            is FloatValue -> FloatValue(value / other.value)
            else -> throw SyntaxException("Invalid operands for division: ${this.name()} and ${other.name()}")
        }

        override fun modulo(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value % other.value)
            is FloatValue -> FloatValue(value % other.value)
            else -> throw SyntaxException("Invalid operands for modulus: ${this.name()} and ${other.name()}")
        }

        override fun pow(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> IntegerValue(value.toDouble().pow(other.value.toDouble()).toInt())
            is FloatValue -> FloatValue(value.toDouble().pow(other.value))
            else -> throw SyntaxException("Invalid operands for power: ${this.name()} and ${other.name()}")
        }

        override fun name(): String {
            return "Integer"
        }

        companion object {
            fun defaultValue(): RuntimeValue = IntegerValue(0)
        }
    }

    data class FloatValue(val value: Double) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value + other.value.toDouble())
            is FloatValue -> FloatValue(value + other.value)
            is StringValue -> StringValue(value.toString() + other.value)
            else -> throw SyntaxException("Invalid operands for addition: ${this.name()} and ${other.name()}")
        }

        override fun minus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value - other.value)
            is FloatValue -> FloatValue(value - other.value)
            else -> throw SyntaxException("Invalid operands for subtraction: ${this.name()} and ${other.name()}")
        }

        override fun times(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value * other.value)
            is FloatValue -> FloatValue(value + other.value)
            else -> throw SyntaxException("Invalid operands for multiplication: ${this.name()} and ${other.name()}")
        }

        override fun divide(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value / other.value)
            is FloatValue -> FloatValue(value / other.value)
            else -> throw SyntaxException("Invalid operands for division: ${this.name()} and ${other.name()}")
        }

        override fun modulo(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value % other.value)
            is FloatValue -> FloatValue(value % other.value)
            else -> throw SyntaxException("Invalid operands for modulus: ${this.name()} and ${other.name()}")
        }

        override fun pow(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> FloatValue(value.pow(other.value))
            is FloatValue -> FloatValue(value.pow(other.value))
            else -> throw SyntaxException("Invalid operands for power: ${this.name()} and ${other.name()}")
        }

        override fun name(): String {
            return "Float"
        }

        companion object {
            fun defaultValue(): RuntimeValue = FloatValue(0.0)
        }
    }

    data class StringValue(val value: String) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> StringValue(value + other.value.toString())
            is FloatValue -> StringValue(value + other.value.toString())
            is StringValue -> StringValue(value + other.value)
            is BooleanValue -> StringValue(value + if (other.value) "true" else "false")
            else -> throw SyntaxException("Invalid operands for addition: ${this.name()} and ${other.name()}")
        }

        override fun minus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for subtraction: ${this.name()} and ${other.name()}")
        }

        override fun times(other: RuntimeValue): RuntimeValue = when (other) {
            is IntegerValue -> StringValue(value.repeat(other.value))
            else -> throw SyntaxException("Invalid operands for multiplication: ${this.name()} and ${other.name()}")
        }

        override fun divide(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for division: ${this.name()} and ${other.name()}")
        }

        override fun modulo(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for modulo: ${this.name()} and ${other.name()}")
        }

        override fun pow(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for power: ${this.name()} and ${other.name()}")
        }

        override fun name(): String {
            return "String"
        }

        companion object {
            fun defaultValue(): RuntimeValue = StringValue("")
        }
    }

    data class BooleanValue(val value: Boolean) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for addition: ${this.name()} and ${other.name()}")
        }

        override fun minus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for subtraction: ${this.name()} and ${other.name()}")
        }

        override fun times(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for multiplication: ${this.name()} and ${other.name()}")
        }

        override fun divide(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for division: ${this.name()} and ${other.name()}")
        }

        override fun modulo(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for modulo: ${this.name()} and ${other.name()}")
        }

        override fun pow(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Invalid operands for power: ${this.name()} and ${other.name()}")
        }

        override fun name(): String {
            return "Boolean"
        }

        companion object {
            fun defaultValue(): RuntimeValue = BooleanValue(false)
        }
    }

    class VoidValue : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Void cannot be part of a mathematical operation")
        }

        override fun minus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Void cannot be part of a mathematical operation")
        }

        override fun times(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Void cannot be part of a mathematical operation")
        }

        override fun divide(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Void cannot be part of a mathematical operation")
        }

        override fun modulo(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Void cannot be part of a mathematical operation")
        }

        override fun pow(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Void cannot be part of a mathematical operation")
        }

        override fun name(): String {
            return "Void"
        }
    }

    data class LambdaValue(val function: Function) : RuntimeValue() {
        override fun plus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Lambda cannot be part of a mathematical operation")
        }

        override fun minus(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Lambda cannot be part of a mathematical operation")
        }

        override fun times(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Lambda cannot be part of a mathematical operation")
        }

        override fun divide(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Lambda cannot be part of a mathematical operation")
        }

        override fun modulo(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Lambda cannot be part of a mathematical operation")
        }

        override fun pow(other: RuntimeValue): RuntimeValue {
            throw SyntaxException("Lambda cannot be part of a mathematical operation")
        }

        override fun name(): String {
            return "Lambda"
        }
    }
}