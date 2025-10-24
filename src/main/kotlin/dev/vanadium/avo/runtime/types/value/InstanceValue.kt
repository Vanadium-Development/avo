package dev.vanadium.avo.runtime.types.value

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.types.ComplexType

class InstanceValue(
    val type: ComplexType,
    val fields: HashMap<String, RuntimeValue>
) : RuntimeValue() {
    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a mathematical operation",
            line
        )
    }

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a mathematical operation",
            line
        )
    }

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a mathematical operation",
            line
        )
    }

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a mathematical operation",
            line
        )
    }

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a mathematical operation",
            line
        )
    }

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a mathematical operation",
            line
        )
    }

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a comparison operation",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a comparison operation",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a comparison operation",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a comparison operation",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Instance value cannot be part of a comparison operation",
            line
        )
    }

    override fun isNumeric(): Boolean {
        return false
    }

    override fun name(): String {
        return type.identifier
    }
}