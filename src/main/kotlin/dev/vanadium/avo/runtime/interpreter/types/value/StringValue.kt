package dev.vanadium.avo.runtime.interpreter.types.value

import dev.vanadium.avo.error.RuntimeError

data class StringValue(val value: String) : RuntimeValue(), KValueMappable {
    override fun toKotlinValue(): Any {
        return value
    }

    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> StringValue(value + other.value.toString())
        is FloatValue   -> StringValue(value + other.value.toString())
        is StringValue  -> StringValue(value + other.value)
        is BooleanValue -> StringValue(value + if (other.value) "true" else "false")
        else            -> throw RuntimeError(
            "Invalid operands for addition: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for subtraction: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> StringValue(value.repeat(other.value))
        else            -> throw RuntimeError(
            "Invalid operands for multiplication: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for division: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for modulo: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for power: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.length > other.value)
        is FloatValue   -> BooleanValue(value.length > other.value)
        is StringValue  -> BooleanValue(value.length > other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for > comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.length < other.value)
        is FloatValue   -> BooleanValue(value.length < other.value)
        is StringValue  -> BooleanValue(value.length < other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for < comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.length >= other.value)
        is FloatValue   -> BooleanValue(value.length >= other.value)
        is StringValue  -> BooleanValue(value.length >= other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for >= comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.length <= other.value)
        is FloatValue   -> BooleanValue(value.length <= other.value)
        is StringValue  -> BooleanValue(value.length <= other.value.length)
        else            -> throw RuntimeError(
            "Invalid operands for <= comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.length == other.value)
        is FloatValue   -> BooleanValue(value.length == other.value.toInt())
        is StringValue  -> BooleanValue(value == other.value)
        else            -> throw RuntimeError(
            "Invalid operands for == comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun isNumeric(): Boolean = false

    override fun name(): String {
        return "String"
    }

    companion object {
        fun defaultValue(): RuntimeValue = StringValue("")
    }
}