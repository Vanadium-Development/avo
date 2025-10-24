package dev.vanadium.avo.runtime.types.value

import dev.vanadium.avo.error.RuntimeError
import kotlin.reflect.KType

class VoidValue : RuntimeValue(), KotlinMappable {
    override fun toKotlinValue(): Any {
        return Unit
    }

    override fun toKotlinType(): KType = dataType().toKType()

    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a mathematical operation",
            line
        )
    }

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a mathematical operation",
            line
        )
    }

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a mathematical operation",
            line
        )
    }

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a mathematical operation",
            line
        )
    }

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a mathematical operation",
            line
        )
    }

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a mathematical operation",
            line
        )
    }

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a comparison",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a comparison",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a comparison",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a comparison",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Void cannot be part of a comparison",
            line
        )
    }

    override fun isNumeric(): Boolean = false

    override fun name(): String {
        return "Void"
    }
}