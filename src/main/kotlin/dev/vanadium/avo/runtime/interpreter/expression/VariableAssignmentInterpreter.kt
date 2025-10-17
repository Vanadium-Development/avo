package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.types.Symbol
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.VariableAssignmentNode

class VariableAssignmentInterpreter(interpreter: Interpreter) : ExpressionInterpreter<VariableAssignmentNode>(
    interpreter
) {
    override fun evaluate(node: VariableAssignmentNode): ControlFlowResult {
        val exprResult = evaluateOther(node.value)

        if (exprResult !is ControlFlowResult.Value)
            throw AvoRuntimeException(
                "Variable assignment value cannot evaluate to a ${exprResult.name()}"
            )

        val expr = exprResult.runtimeValue

        val exprType = expr.dataType()
        val variable = scope.getSymbol(node.identifier.value)
        if (variable !is Symbol.Variable)
            throw AvoRuntimeException(
                "Symbol \"${node.identifier.value}\" is not a variable"
            )

        if (exprType != variable.type) {
            throw AvoRuntimeException(
                "Cannot assign expression of type $exprType " +
                        "to variable \"${node.identifier.value}\" of type ${variable.type}"
            )
        }
        scope.assignVariable(node.identifier.value, expr)
        return exprResult
    }
}