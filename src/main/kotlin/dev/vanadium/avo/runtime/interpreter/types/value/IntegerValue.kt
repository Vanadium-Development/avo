package dev.vanadium.avo.runtime.interpreter.types.value

import dev.vanadium.avo.error.RuntimeError
import kotlin.math.pow

data class IntegerValue(val value: Int) : RuntimeValue(), KValueMappable {
    override fun toKotlinValue(): Any {
        return value
    }

    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> IntegerValue(value + other.value)
        is FloatValue   -> FloatValue(value + other.value)
        is StringValue  -> StringValue(value.toString() + other.value)
        else            -> throw RuntimeError(
            "Invalid operands for addition: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> IntegerValue(value - other.value)
        is FloatValue   -> FloatValue(value - other.value)
        else            -> throw RuntimeError(
            "Invalid operands for subtraction: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> IntegerValue(value * other.value)
        is FloatValue   -> FloatValue(value * other.value)
        is StringValue  -> StringValue(other.value.repeat(value))
        else            -> throw RuntimeError(
            "Invalid operands for multiplication: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> IntegerValue(value / other.value)
        is FloatValue   -> FloatValue(value / other.value)
        else            -> throw RuntimeError(
            "Invalid operands for division: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> IntegerValue(value % other.value)
        is FloatValue   -> FloatValue(value % other.value)
        else            -> throw RuntimeError(
            "Invalid operands for modulus: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> IntegerValue(value.toDouble().pow(other.value.toDouble()).toInt())
        is FloatValue   -> FloatValue(value.toDouble().pow(other.value))
        else            -> throw RuntimeError(
            "Invalid operands for power: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value > other.value)
        is FloatValue   -> BooleanValue(value > other.value)
        is StringValue  -> BooleanValue(value > other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for > comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value < other.value)
        is FloatValue   -> BooleanValue(value < other.value)
        is StringValue  -> BooleanValue(value < other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for < comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value >= other.value)
        is FloatValue   -> BooleanValue(value >= other.value)
        is StringValue  -> BooleanValue(value >= other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for >= comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value <= other.value)
        is FloatValue   -> BooleanValue(value <= other.value)
        is StringValue  -> BooleanValue(value <= other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for <= comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value == other.value)
        is FloatValue   -> BooleanValue(value == other.value.toInt())
        is StringValue  -> BooleanValue(value == other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for == comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun isNumeric(): Boolean = true

    override fun name(): String {
        return "Integer"
    }

    companion object {
        fun defaultValue(): RuntimeValue = IntegerValue(0)
    }
}