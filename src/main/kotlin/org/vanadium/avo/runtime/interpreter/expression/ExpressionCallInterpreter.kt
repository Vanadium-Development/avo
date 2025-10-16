package org.vanadium.avo.runtime.interpreter.expression

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.Scope
import org.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.ExpressionCallNode
import org.vanadium.avo.syntax.ast.ExpressionNode
import org.vanadium.avo.syntax.ast.ReturnStatementNode
import org.vanadium.avo.types.DataType

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

        for (node in function.block.nodes) {
            if (node is ReturnStatementNode) {
                returnValue = evaluateOther(node.expression)
                break
            }
            if (node is ExpressionNode) {
                evaluateOther(node)
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