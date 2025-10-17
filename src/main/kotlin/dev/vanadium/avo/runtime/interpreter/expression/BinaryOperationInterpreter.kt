package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.syntax.ast.BinaryOperationNode
import dev.vanadium.avo.syntax.ast.BinaryOperationType

class BinaryOperationInterpreter(
    interpreter: Interpreter
) : ExpressionInterpreter<BinaryOperationNode>(interpreter) {
    override fun evaluate(node: BinaryOperationNode): ControlFlowResult {
        val leftResult = evaluateOther(node.left)
        val rightResult = evaluateOther(node.right)

        if (leftResult !is ControlFlowResult.Value) {
            throw AvoRuntimeException(
                "Binary expression cannot evaluate to a ${leftResult.name()}"
            )
        }

        if (rightResult !is ControlFlowResult.Value) {
            throw AvoRuntimeException(
                "Binary expression cannot evaluate to a ${rightResult.name()}"
            )
        }

        val left = leftResult.runtimeValue
        val right = rightResult.runtimeValue

        return ControlFlowResult.Value(
            when (node.type) {
                BinaryOperationType.PLUS -> left.plus(right)
                BinaryOperationType.MINUS -> left.minus(right)
                BinaryOperationType.MULTIPLY -> left.times(right)
                BinaryOperationType.DIVIDE -> left.divide(right)
                BinaryOperationType.MODULUS -> left.modulo(right)
                BinaryOperationType.POWER -> left.pow(right)
                BinaryOperationType.GREATER_THAN -> left.greaterThan(right)
                BinaryOperationType.LESS_THAN -> left.lessThan(right)
                BinaryOperationType.GREATER_EQUAL -> left.greaterThanOrEqualTo(right)
                BinaryOperationType.LESS_EQUAL -> left.lessThanOrEqualTo(right)
                BinaryOperationType.EQUALS -> left.equal(right)
            }
        )
    }

}