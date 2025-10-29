package dev.vanadium.avo.runtime.types.value

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.types.symbol.Namespace

class NamespaceValue(
    val namespace: Namespace
) : RuntimeValue() {
    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = throw RuntimeError(
        "Cannot perform operations on a namespace",
        line
    )

    override fun isNumeric(): Boolean = false

    override fun name(): String = "Namespace"
}