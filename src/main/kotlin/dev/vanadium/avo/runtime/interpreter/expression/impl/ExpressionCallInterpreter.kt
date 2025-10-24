package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.LambdaValue
import dev.vanadium.avo.syntax.ast.ExpressionCallNode

@ExpressionInterpreterImplementation
class ExpressionCallInterpreter(runtime: Runtime) : ExpressionInterpreter<ExpressionCallNode>(runtime) {
    override fun evaluate(node: ExpressionCallNode): ControlFlowResult {
        val expressionResult = evaluateOther(node.expression)

        if (expressionResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Expression to be called cannot evaluate to a ${expressionResult.name()}",
                node.line
            )

        val expression = expressionResult.runtimeValue

        if (expression !is LambdaValue)
            throw RuntimeError(
                "Expression is not callable",
                node.line
            )

        val function = expression.function

        if (function.signature.size != node.parameters.size)
            throw RuntimeError(
                "Function \"${function.name()}\" expected ${function.signature.size} parameters, but " +
                "received ${node.parameters.size}",
                node.line
            )

        // The usable function scope is a new child scope of the captured scope
        val functionScope = Scope(function.scope)

        val params = function.signature.zip(node.parameters)
        params.forEachIndexed { i, param ->
            val valueResult = evaluateOther(param.second.expression)

            if (valueResult !is ControlFlowResult.Value)
                throw RuntimeError(
                    "Function call parameter cannot evaluate to a ${valueResult.name()}",
                    node.line
                )

            val value = valueResult.runtimeValue

            if (param.first.type != value.dataType())
                throw RuntimeError(
                    "Parameter \"${param.first.identifier.value}\" of function \"${function.name()}\" " +
                    "is declared with type ${param.first.type} but was called with ${value.dataType()}",
                    node.line
                )

            // Declare signature variables in the function scope
            functionScope.declareVariable(param.first.identifier.value, value.dataType(), value, node.line)
        }

        pushScope(functionScope)

        val returnResult: ControlFlowResult = evaluateOther(function.block)

        if (returnResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Unexpected ${returnResult.name()} in Function",
                function.block.line
            )

        val returnValue = returnResult.runtimeValue

        if (function.returnType != returnValue.dataType()) {
            throw RuntimeError(
                "Function ${function.returnType} of type ${function.returnType} returns ${returnValue.dataType()} on a control path",
                function.block.line
            )
        }

        // Leave the function scope
        popScope()

        return returnResult
    }
}