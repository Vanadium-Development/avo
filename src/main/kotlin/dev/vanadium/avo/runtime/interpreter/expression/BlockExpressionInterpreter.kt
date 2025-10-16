package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.BlockExpressionNode
import dev.vanadium.avo.syntax.ast.ExpressionNode
import dev.vanadium.avo.syntax.ast.InternalFunctionDefinitionNode
import dev.vanadium.avo.syntax.ast.ReturnStatementNode

class BlockExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<BlockExpressionNode>(interpreter) {

    override fun evaluate(node: BlockExpressionNode): RuntimeValue {
        var returnValue: RuntimeValue = RuntimeValue.VoidValue()
        for (blockNode in node.nodes) {
            if (blockNode is InternalFunctionDefinitionNode) {
                if (node.parent == null)
                    throw AvoRuntimeException("Internal function definition must be in the root scope")


            }
            if (blockNode is ReturnStatementNode) {
                returnValue = evaluateOther(blockNode.expression)
                break
            }
            if (blockNode is ExpressionNode) {
                evaluateOther(blockNode)
            }
        }
        return returnValue
    }

}