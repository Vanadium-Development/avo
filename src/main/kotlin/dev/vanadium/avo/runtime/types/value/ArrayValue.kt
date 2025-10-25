package dev.vanadium.avo.runtime.types.value

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.types.DataType

class ArrayValue(
    val type: DataType.ArrayType,
    val value: MutableList<RuntimeValue>
) : RuntimeValue() {
    override fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        val otherType = other.dataType()
        if (otherType != type.elementType)
            throw RuntimeError(
                "Cannot append value of type $otherType to an array of type ${type.elementType}",
                line
            )

        value.add(other)
        return this
    }

    override fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        if (other !is IntegerValue)
            throw RuntimeError(
                "Invalid index type ${other.dataType()} for array remove operation",
                line
            )

        val index = other.value
        val size = value.size

        if (index !in 0..<size)
            throw RuntimeError(
                "Cannot remove index $index of array of size $size: Out of bounds",
                line
            )

        value.removeAt(index)
        return this
    }

    override fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        if (other !is IntegerValue)
            throw RuntimeError(
                "Expression of type ${other.dataType()} cannot be used for array repeat operation",
                line
            )

        val count = other.value

        if (count < 0)
            throw RuntimeError(
                "Cannot repeat array $count times",
                line
            )

        if (count == 0)
            value.clear()

        if (count > 1) {
            val original = value.toList()
            repeat(other.value -1) {
                value.addAll(original)
            }
        }

        return this
    }

    override fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "An array cannot be divided",
            line
        )
    }

    override fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Cannot take the module of an array",
            line
        )
    }

    override fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue {
        throw RuntimeError(
            "Cannot raise an array to a power",
            line
        )
    }

    override fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.size > other.value)
        is ArrayValue -> BooleanValue(value.size > other.value.size)
        else -> throw RuntimeError(
            "Cannot compare array with value of type ${other.dataType()}",
            line
        )
    }

    override fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.size < other.value)
        is ArrayValue -> BooleanValue(value.size < other.value.size)
        else -> throw RuntimeError(
            "Cannot compare array with value of type ${other.dataType()}",
            line
        )
    }

    override fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.size >= other.value)
        is ArrayValue -> BooleanValue(value.size >= other.value.size)
        else -> throw RuntimeError(
            "Cannot compare array with value of type ${other.dataType()}",
            line
        )
    }

    override fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.size <= other.value)
        is ArrayValue -> BooleanValue(value.size <= other.value.size)
        else -> throw RuntimeError(
            "Cannot compare array with value of type ${other.dataType()}",
            line
        )
    }

    override fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue = when (other) {
        is IntegerValue -> BooleanValue(value.size == other.value)
        is ArrayValue -> BooleanValue(value == other.value)
        else -> throw RuntimeError(
            "Cannot compare array to value of type ${other.dataType()}",
            line
        )
    }

    override fun isNumeric(): Boolean = false

    override fun name(): String = "Array"
}