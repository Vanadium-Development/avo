package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.syntax.ast.InternalFunctionCallNode

@ExpressionInterpreterImplementation
class InternalFunctionCallInterpreter(runtime: Runtime) : ExpressionInterpreter<InternalFunctionCallNode>(
    runtime
) {

    override fun evaluate(node: InternalFunctionCallNode): ControlFlowResult {
        val parameters = mutableListOf<RuntimeValue>()
        node.parameters.forEachIndexed { index, param ->
            val paramResult = evaluateOther(param.expression)
            if (paramResult !is ControlFlowResult.Value)
                throw RuntimeError(
                    "Unexpected ${paramResult.name()} in parameter $index of internal function call ${node.identifier.value}",
                    node.line
                )
            parameters.add(paramResult.runtimeValue)
        }
        return ControlFlowResult.Value(
            runtime.functionLoader.invokeFunction(
                node.line,
                node.identifier.value,
                parameters
            )
        )
    }

}