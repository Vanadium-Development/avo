package dev.vanadium.avo.runtime.interpreter.types

import dev.vanadium.avo.error.RuntimeError
import kotlin.reflect.KType
import kotlin.reflect.full.createType

interface KTypeMappable {
    fun toKType(): KType
}

sealed class DataType : KTypeMappable {
    abstract override fun toString(): String

    object InferredType : DataType() {
        override fun toString() = "<inferred>"
        override fun toKType(): KType {
            throw RuntimeError("${this.javaClass.simpleName} cannot be mapped to a kotlin type", 0)
        }
    }

    object IntegerType : DataType(), KTypeMappable {
        override fun toString() = "int"
        override fun toKType(): KType = Int::class.createType()
    }

    object FloatType : DataType(), KTypeMappable {
        override fun toString() = "float"
        override fun toKType(): KType = Double::class.createType()
    }

    object StringType : DataType(), KTypeMappable {
        override fun toString() = "string"
        override fun toKType(): KType = String::class.createType()
    }

    object BooleanType : DataType(), KTypeMappable {
        override fun toString() = "boolean"
        override fun toKType(): KType = Boolean::class.createType()
    }

    object VoidType : DataType() {
        override fun toString() = "void"
        override fun toKType(): KType = Unit::class.createType()
    }

    data class LambdaType(
        val signature: List<DataType>,
        val returnType: DataType
    ) : DataType() {
        override fun toString() = "[(${signature.joinToString(", ") { it.toString() }}) -> $returnType]"
        override fun toKType(): KType {
            throw RuntimeError("${this.javaClass.simpleName} cannot be mapped to a kotlin type", 0)
        }
    }

    data class ComplexType(val name: String) : DataType() {
        override fun toKType(): KType {
            throw RuntimeError("${this.javaClass.simpleName} cannot be mapped to a kotlin type", 0)
        }
    }
}