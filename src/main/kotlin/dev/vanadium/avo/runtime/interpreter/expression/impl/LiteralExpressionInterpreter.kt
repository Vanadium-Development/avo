package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.LiteralNode

@ExpressionInterpreterImplementation
class LiteralExpressionInterpreter(runtime: Runtime) : ExpressionInterpreter<LiteralNode>(runtime) {
    override fun evaluate(node: LiteralNode): ControlFlowResult = ControlFlowResult.Value(node.runtimeValue())
}