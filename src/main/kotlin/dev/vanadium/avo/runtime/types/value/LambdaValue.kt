package dev.vanadium.avo.runtime.types.value

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.types.Symbol

data class LambdaValue(val function: Symbol.Function) : RuntimeValue() {
    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a mathematical operation",
            line
        )
    }

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a mathematical operation",
            line
        )
    }

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a mathematical operation",
            line
        )
    }

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a mathematical operation",
            line
        )
    }

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a mathematical operation",
            line
        )
    }

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a mathematical operation",
            line
        )
    }

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a comparison",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a comparison",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a comparison",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a comparison",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Lambda cannot be part of a comparison",
            line
        )
    }

    override fun isNumeric(): Boolean = false

    override fun name(): String {
        return "Lambda"
    }
}