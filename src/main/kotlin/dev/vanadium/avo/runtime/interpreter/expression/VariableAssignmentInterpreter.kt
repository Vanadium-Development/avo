package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.InterpreterImpl
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.Symbol
import dev.vanadium.avo.syntax.ast.VariableAssignmentNode

@InterpreterImpl
class VariableAssignmentInterpreter(interpreter: Interpreter) : ExpressionInterpreter<VariableAssignmentNode>(
    interpreter
) {
    override fun evaluate(node: VariableAssignmentNode): ControlFlowResult {
        val exprResult = evaluateOther(node.value)

        if (exprResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Variable assignment value cannot evaluate to a ${exprResult.name()}",
                node.line
            )

        val expr = exprResult.runtimeValue

        val exprType = expr.dataType()
        val variable = scope.getSymbol(node.identifier.value, node.line)
        if (variable !is Symbol.Variable)
            throw RuntimeError(
                "Symbol \"${node.identifier.value}\" is not a variable",
                node.line
            )

        if (exprType != variable.type) {
            throw RuntimeError(
                "Cannot assign expression of type $exprType " +
                "to variable \"${node.identifier.value}\" of type ${variable.type}",
                node.line
            )
        }
        scope.assignVariable(node.identifier.value, expr, node.line)
        return exprResult
    }
}