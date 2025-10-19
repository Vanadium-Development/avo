package dev.vanadium.avo.runtime.interpreter.types

import kotlin.reflect.KType
import kotlin.reflect.full.createType

interface KTypeMappable {
    fun toKType(): KType
}

sealed class DataType {
    abstract override fun toString(): String

    object InferredType : DataType() {
        override fun toString() = "Inferred"
    }

    object IntegerType : DataType(), KTypeMappable {
        override fun toString() = "Integer"
        override fun toKType(): KType = Int::class.createType()
    }

    object FloatType : DataType(), KTypeMappable{
        override fun toString() = "Float"
        override fun toKType(): KType = Double::class.createType()
    }

    object StringType : DataType(), KTypeMappable {
        override fun toString() = "String"
        override fun toKType(): KType = String::class.createType()
    }

    object BooleanType : DataType(), KTypeMappable {
        override fun toString() = "Boolean"
        override fun toKType(): KType = Boolean::class.createType()
    }

    object VoidType : DataType() {
        override fun toString() = "Void"
    }

    data class LambdaType(
        val signature: List<DataType>,
        val returnType: DataType
    ) : DataType() {
        override fun toString() = "[(${signature.joinToString(", ") { it.toString() }}) -> $returnType]"
    }

    data class ComplexType(val name: String) : DataType()
}