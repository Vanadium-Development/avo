package org.vanadium.avo.runtime

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.syntax.ast.BinaryOperationNode
import org.vanadium.avo.syntax.ast.BinaryOperationType
import org.vanadium.avo.syntax.ast.ExpressionNode
import org.vanadium.avo.syntax.ast.LiteralNode

class Interpreter(val scope: Scope) {

    fun eval(node: ExpressionNode): RuntimeValue = when (node) {
        is BinaryOperationNode -> evalBinaryExpressionNode(node)
        is LiteralNode -> evalLiteralExpressionNode(node)
        else -> throw AvoRuntimeException(
            "Could not evaluate expression node ${node.javaClass.simpleName}"
        )
    }

    private fun evalBinaryExpressionNode(node: BinaryOperationNode): RuntimeValue {
        val left = eval(node.left)
        val right = eval(node.right)

        return when (node.type) {
            BinaryOperationType.PLUS -> left.plus(right)
            BinaryOperationType.MINUS -> left.minus(right)
            BinaryOperationType.MULTIPLY -> left.times(right)
            else -> throw AvoRuntimeException("Unsupported operation type ${node.type}")
        }
    }

    private fun evalLiteralExpressionNode(node: LiteralNode): RuntimeValue = node.runtimeValue()

}