package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.MemberAccessNode

@ExpressionInterpreterImplementation
class MemberAccessInterpreter(runtime: Runtime) : ExpressionInterpreter<MemberAccessNode>(runtime) {
    override fun evaluate(node: MemberAccessNode): ControlFlowResult {
        // TODO
        // TODO
        // TODO
        return super.evaluate(node)
    }
}