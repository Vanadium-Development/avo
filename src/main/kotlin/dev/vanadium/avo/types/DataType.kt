package dev.vanadium.avo.types

import kotlin.reflect.KClassifier

sealed class DataType {
    abstract override fun toString(): String

    object InferredType : DataType() {
        override fun toString() = "Inferred"
    }

    object IntegerType : DataType() {
        override fun toString() = "Integer"
    }

    object FloatType : DataType() {
        override fun toString() = "Float"
    }

    object StringType : DataType() {
        override fun toString() = "String"
    }

    object BooleanType : DataType() {
        override fun toString() = "Boolean"
    }

    object VoidType : DataType() {
        override fun toString() = "Void"
    }

    data class LambdaType(
        val signature: List<DataType>,
        val returnType: DataType
    ) : DataType() {
        override fun toString() = "(${signature.joinToString(", ") { it.toString() }}) -> $returnType"
    }

    data class ComplexType(val name: String) : DataType()
}