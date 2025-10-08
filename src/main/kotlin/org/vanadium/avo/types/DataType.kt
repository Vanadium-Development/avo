package org.vanadium.avo.types

sealed class DataType {
    object InferredType: DataType()
    object IntegerType : DataType()
    object FloatType : DataType()
    object StringType : DataType()
    object BooleanType : DataType()
    object VoidType: DataType()
    data class ComplexType(val name: String) : DataType()
}