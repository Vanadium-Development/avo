package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.VoidValue
import dev.vanadium.avo.syntax.ast.*

@ExpressionInterpreterImplementation
class BlockExpressionInterpreter(runtime: Runtime) : ExpressionInterpreter<BlockExpressionNode>(runtime) {

    override fun evaluate(node: BlockExpressionNode): ControlFlowResult {
        for (blockNode in node.nodes) {
            val expr = when (blockNode) {
                is ReturnStatementNode       -> {
                    val returnResult = evaluateOther(blockNode.expression)
                    if (returnResult !is ControlFlowResult.Value)
                        throw RuntimeError(
                            "Return expression cannot evaluate to a ${returnResult.name()}",
                            blockNode.line
                        )
                    return ControlFlowResult.Return(returnResult.runtimeValue)
                }

                is ContinueStatementNode     -> return ControlFlowResult.Continue()
                is BreakStatementNode        -> return ControlFlowResult.Break()
                is ExpressionNode            -> evaluateOther(blockNode)
                is ComplexTypeDefinitionNode -> runtime.execute(blockNode)
                else                         -> throw RuntimeError(
                    "Unexpected node in block: ${blockNode.javaClass.simpleName}",
                    blockNode.line
                )
            }

            if (expr is ControlFlowResult.Return || expr is ControlFlowResult.Continue || expr is ControlFlowResult.Break) {
                return expr
            }
        }
        return ControlFlowResult.Value(VoidValue())
    }

}