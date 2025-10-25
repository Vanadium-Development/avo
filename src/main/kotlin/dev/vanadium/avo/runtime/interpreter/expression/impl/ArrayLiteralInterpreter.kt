package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.runtime.types.value.ArrayValue
import dev.vanadium.avo.syntax.ast.ArrayLiteralNode

@ExpressionInterpreterImplementation
class ArrayLiteralInterpreter(runtime: Runtime) :
    ExpressionInterpreter<ArrayLiteralNode>(runtime) {

    override fun evaluate(node: ArrayLiteralNode): ControlFlowResult {
        var elementType: DataType? = null
        val values = node.values.mapIndexed { index, it ->
            val exprResult = evaluateOther(it)
            if (exprResult !is ControlFlowResult.Value)
                throw RuntimeError(
                    "Array literal element cannot evaluate to a $exprResult",
                    node.line
                )

            val value = exprResult.runtimeValue
            val valueType = value.dataType()

            if (elementType == null)
                elementType = valueType

            if (elementType != valueType)
                throw RuntimeError(
                    "Inconsistent types in array literal: First element is of type $elementType, but element $index is of type $valueType",
                    node.line
                )

            return@mapIndexed value
        }

        if (elementType == null)
            throw RuntimeError(
                "Array literal must have at least one element",
                node.line
            )

        return ControlFlowResult.Value(
            ArrayValue(
                DataType.ArrayType(
                    elementType
                ), values.toMutableList()
            )
        )
    }
}