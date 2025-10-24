package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.BinaryOperationNode
import dev.vanadium.avo.syntax.ast.BinaryOperationType

@ExpressionInterpreterImplementation
class BinaryOperationInterpreter(
    runtime: Runtime
) : ExpressionInterpreter<BinaryOperationNode>(runtime) {
    override fun evaluate(node: BinaryOperationNode): ControlFlowResult {
        val leftResult = evaluateOther(node.left)
        val rightResult = evaluateOther(node.right)

        if (leftResult !is ControlFlowResult.Value) {
            throw RuntimeError(
                "Binary expression cannot evaluate to a ${leftResult.name()}",
                node.line
            )
        }

        if (rightResult !is ControlFlowResult.Value) {
            throw RuntimeError(
                "Binary expression cannot evaluate to a ${rightResult.name()}",
                node.line
            )
        }

        val left = leftResult.runtimeValue
        val right = rightResult.runtimeValue

        val line = node.line

        return ControlFlowResult.Value(
            when (node.type) {
                BinaryOperationType.PLUS          -> left.plus(right, line)
                BinaryOperationType.MINUS         -> left.minus(right, line)
                BinaryOperationType.MULTIPLY      -> left.times(right, line)
                BinaryOperationType.DIVIDE        -> left.divide(right, line)
                BinaryOperationType.MODULUS       -> left.modulo(right, line)
                BinaryOperationType.POWER         -> left.pow(right, line)
                BinaryOperationType.GREATER_THAN  -> left.greaterThan(right, line)
                BinaryOperationType.LESS_THAN     -> left.lessThan(right, line)
                BinaryOperationType.GREATER_EQUAL -> left.greaterThanOrEqualTo(right, line)
                BinaryOperationType.LESS_EQUAL    -> left.lessThanOrEqualTo(right, line)
                BinaryOperationType.EQUALS        -> left.equal(right, line)
            }
        )
    }

}