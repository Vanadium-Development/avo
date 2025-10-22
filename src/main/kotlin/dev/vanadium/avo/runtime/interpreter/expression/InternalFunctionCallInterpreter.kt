package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.InterpreterImpl
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.value.RuntimeValue
import dev.vanadium.avo.syntax.ast.InternalFunctionCallNode

@InterpreterImpl
class InternalFunctionCallInterpreter(interpreter: Interpreter) : ExpressionInterpreter<InternalFunctionCallNode>(
    interpreter
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
            interpreter.functionLoader.invokeFunction(
                node.line,
                node.identifier.value,
                parameters
            )
        )
    }

}