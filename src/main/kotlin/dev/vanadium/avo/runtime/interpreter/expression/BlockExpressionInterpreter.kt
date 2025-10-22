package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.InterpreterImpl
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.value.VoidValue
import dev.vanadium.avo.syntax.ast.*

@InterpreterImpl
class BlockExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<BlockExpressionNode>(interpreter) {

    override fun evaluate(node: BlockExpressionNode): ControlFlowResult {
        for (blockNode in node.nodes) {
            val expr = when (blockNode) {
                is ReturnStatementNode   -> return evaluateOther(blockNode.expression)
                is ContinueStatementNode -> return ControlFlowResult.Continue()
                is BreakStatementNode    -> return ControlFlowResult.Break()
                is ExpressionNode        -> evaluateOther(blockNode)
                else                     -> throw RuntimeError(
                    "Unexpected node in block: ${blockNode.javaClass.simpleName}",
                    blockNode.line
                )
            }

            if (expr is ControlFlowResult.Break || expr is ControlFlowResult.Continue) {
                return expr
            }
        }
        return ControlFlowResult.Value(VoidValue())
    }

}