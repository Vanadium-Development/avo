package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.BinaryOperationNode
import dev.vanadium.avo.syntax.ast.BinaryOperationType

class BinaryOperationInterpreter(
    interpreter: Interpreter
) : ExpressionInterpreter<BinaryOperationNode>(interpreter) {
    override fun evaluate(node: BinaryOperationNode): RuntimeValue {
        val left = evaluateOther(node.left)
        val right = evaluateOther(node.right)

        return when (node.type) {
            BinaryOperationType.PLUS -> left.plus(right)
            BinaryOperationType.MINUS -> left.minus(right)
            BinaryOperationType.MULTIPLY -> left.times(right)
            BinaryOperationType.DIVIDE -> left.divide(right)
            BinaryOperationType.MODULUS -> left.modulo(right)
            BinaryOperationType.POWER -> left.pow(right)
        }
    }

}