package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.ExpressionNode

abstract class ExpressionInterpreter<T : ExpressionNode>(val runtime: Runtime) {

    protected val scope get() = runtime.scope

    open fun evaluate(node: T): ControlFlowResult {
        TODO("Interpreter Not Implemented")
    }

    fun evaluateOther(node: ExpressionNode): ControlFlowResult = runtime.evaluate(node)

    fun pushScope(scope: Scope) {
        runtime.scopes.push(scope)
    }

    fun popScope() {
        runtime.scopes.pop()
    }
}