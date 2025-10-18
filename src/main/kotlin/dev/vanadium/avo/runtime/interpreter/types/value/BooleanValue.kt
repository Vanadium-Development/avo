package dev.vanadium.avo.runtime.interpreter.types.value

import dev.vanadium.avo.error.RuntimeError

data class BooleanValue(val value: Boolean) : RuntimeValue() {
    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
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
    ): RuntimeValue {
        throw RuntimeError(
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
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for > comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for < comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for >= comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for <= comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Invalid operands for == comparison: ${this.name()} and ${other.name()}",
            line
        )
    }

    override fun isNumeric(): Boolean = false

    override fun name(): String {
        return "Boolean"
    }

    companion object {
        fun defaultValue(): RuntimeValue = BooleanValue(false)
    }
}