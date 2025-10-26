package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.ArrayValue
import dev.vanadium.avo.runtime.types.value.IntegerValue
import dev.vanadium.avo.syntax.ast.IndexAccessNode

@ExpressionInterpreterImplementation
class IndexAccessInterpreter(runtime: Runtime) : ExpressionInterpreter<IndexAccessNode>(runtime) {

    override fun evaluate(node: IndexAccessNode): ControlFlowResult {
        val targetResult = evaluateOther(node.target)

        if (targetResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Index access target cannot valuate to a ${targetResult.name()}",
                node.line
            )

        val target = targetResult.runtimeValue

        if (target !is ArrayValue)
            throw RuntimeError(
                "Cannot perform index access on type ${target.dataType()}",
                node.line
            )

        val indexResult = evaluateOther(node.index)

        if (indexResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Index expression cannot evaluate to a ${indexResult.name()}",
                node.line
            )

        val indexValue = indexResult.runtimeValue

        if (indexValue !is IntegerValue)
            throw RuntimeError(
                "Cannot use value of type ${indexValue.dataType()} as index",
                node.line
            )

        val index = indexValue.value
        val arraySize = target.value.size

        if (index !in 0..<arraySize)
            throw RuntimeError(
                "Cannot access element at index $index in array of size $arraySize",
                node.line
            )

        return ControlFlowResult.Value(
            target.value[index]
        )
    }
}