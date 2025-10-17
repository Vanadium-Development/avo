package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.syntax.ast.*

class BlockExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<BlockExpressionNode>(interpreter) {

    override fun evaluate(node: BlockExpressionNode): ControlFlowResult {
        for (blockNode in node.nodes) {
            val expr = when (blockNode) {
                is ReturnStatementNode   -> return evaluateOther(blockNode.expression)
                is ContinueStatementNode -> return ControlFlowResult.Continue()
                is BreakStatementNode    -> return ControlFlowResult.Break()
                is ExpressionNode        -> evaluateOther(blockNode)
                else                     -> throw AvoRuntimeException(
                    "Unexpected node in block: ${blockNode.javaClass.simpleName}"
                )
            }

            if (expr is ControlFlowResult.Break || expr is ControlFlowResult.Continue) {
                return expr
            }
        }
        return ControlFlowResult.Value(RuntimeValue.VoidValue())
    }

}