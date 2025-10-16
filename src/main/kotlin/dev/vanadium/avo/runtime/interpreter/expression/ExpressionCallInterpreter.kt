package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.ExpressionCallNode
import dev.vanadium.avo.syntax.ast.ExpressionNode
import dev.vanadium.avo.syntax.ast.ReturnStatementNode
import dev.vanadium.avo.types.DataType

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

        var returnValue: RuntimeValue? = null

        for (fnNode in function.block.nodes) {
            if (fnNode is ReturnStatementNode) {
                returnValue = evaluateOther(fnNode.expression)
                break
            }
            if (fnNode is ExpressionNode) {
                evaluateOther(fnNode)
            }
        }

        // Leave the function scope
        popScope()

        return returnValue ?: (if (function.returnType is DataType.VoidType) RuntimeValue.VoidValue()
        else throw AvoRuntimeException(
            "Function \"${function.name()}\" does not return a value on all paths"
        ))
    }
}