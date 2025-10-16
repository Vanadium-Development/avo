package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.ExpressionCallNode

class ExpressionCallInterpreter(interpreter: Interpreter) : ExpressionInterpreter<ExpressionCallNode>(interpreter) {
    override fun evaluate(node: ExpressionCallNode): RuntimeValue {
        val expression = evaluateOther(node.expression)

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
            val value = evaluateOther(param.second.expression)
            if (param.first.type != value.dataType())
                throw AvoRuntimeException(
                    "Parameter \"${param.first.identifier.value}\" of function \"${function.name()}\" " +
                            "is declared with type $value but received ${param.first.type}"
                )

            // Declare signature variables in the function scope
            scope.declareVariable(param.first.identifier.value, value.dataType(), value)
        }

        val returnValue: RuntimeValue = evaluateOther(function.block)

        if (function.returnType != returnValue.dataType()) {
            throw AvoRuntimeException("Function ${function.returnType} of type \"\" returns ${returnValue.dataType()} on a control path")
        }

        // Leave the function scope
        popScope()

        return returnValue
    }
}