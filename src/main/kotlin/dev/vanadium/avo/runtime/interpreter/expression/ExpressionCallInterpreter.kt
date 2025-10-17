package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.syntax.ast.ExpressionCallNode

class ExpressionCallInterpreter(interpreter: Interpreter) : ExpressionInterpreter<ExpressionCallNode>(interpreter) {
    override fun evaluate(node: ExpressionCallNode): ControlFlowResult {
        val expressionResult = evaluateOther(node.expression)

        if (expressionResult !is ControlFlowResult.Value)
            throw AvoRuntimeException(
                "Expression to be called cannot evaluate to a ${expressionResult.name()}"
            )

        val expression = expressionResult.runtimeValue

        if (expression !is RuntimeValue.LambdaValue)
            throw AvoRuntimeException(
                "Expression is not callable"
            )

        val function = expression.function

        if (function.signature.size != node.parameters.size)
            throw AvoRuntimeException(
                "Function \"${function.name()}\" expected ${function.signature.size} parameters, but " +
                "received ${node.parameters.size}"
            )

        // The usable function scope is a new child scope of the captured scope
        val functionScope = Scope(function.scope)
        pushScope(functionScope)

        val params = function.signature.zip(node.parameters)
        params.forEachIndexed { i, param ->
            val valueResult = evaluateOther(param.second.expression)

            if (valueResult !is ControlFlowResult.Value)
                throw AvoRuntimeException(
                    "Function call parameter cannot evaluate to a ${valueResult.name()}"
                )

            val value = valueResult.runtimeValue

            if (param.first.type != value.dataType())
                throw AvoRuntimeException(
                    "Parameter \"${param.first.identifier.value}\" of function \"${function.name()}\" " +
                    "is declared with type $value but received ${param.first.type}"
                )

            // Declare signature variables in the function scope
            scope.declareVariable(param.first.identifier.value, value.dataType(), value)
        }

        val returnResult: ControlFlowResult = evaluateOther(function.block)

        if (returnResult !is ControlFlowResult.Value)
            throw AvoRuntimeException(
                "Unexpected ${returnResult.name()} in Function"
            )

        val returnValue = returnResult.runtimeValue

        if (function.returnType != returnValue.dataType()) {
            throw AvoRuntimeException("Function ${function.returnType} of type ${function.returnType} returns ${returnValue.dataType()} on a control path")
        }

        // Leave the function scope
        popScope()

        return returnResult
    }
}