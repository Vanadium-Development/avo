package dev.vanadium.avo.runtime.interpreter.types.value

import dev.vanadium.avo.runtime.interpreter.types.DataType
import kotlin.reflect.KType

interface KotlinMappable {
    fun toKotlinValue(): Any
    fun toKotlinType(): KType
}

sealed class RuntimeValue {
    companion object {
        fun fromKotlinValue(value: Any): RuntimeValue {
            return when (value) {
                is Boolean -> BooleanValue(value)
                is String  -> StringValue(value)
                is Double  -> FloatValue(value)
                is Int     -> IntegerValue(value)
                is Unit    -> VoidValue()
                else       -> throw RuntimeException(
                    "Kotlin value of type ${value.javaClass.simpleName} cannot be converted to an Avo value"
                )
            }
        }
    }

    // Mathematical Operations
    abstract fun plus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun minus(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun times(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun divide(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun modulo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun pow(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    // Comparisons
    abstract fun greaterThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun lessThan(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun greaterThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun lessThanOrEqualTo(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    abstract fun equal(
        other: RuntimeValue,
        line: Int
    ): RuntimeValue

    // Utility
    abstract fun isNumeric(): Boolean
    abstract fun name(): String

    fun dataType() = when (this) {
        is IntegerValue -> DataType.IntegerType
        is FloatValue   -> DataType.FloatType
        is StringValue  -> DataType.StringType
        is BooleanValue -> DataType.BooleanType
        is VoidValue    -> DataType.VoidType
        is LambdaValue  -> DataType.LambdaType(this.function.signature.map { it.type }, this.function.returnType)
    }

}