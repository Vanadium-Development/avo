package dev.vanadium.avo.runtime.interpreter

import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.syntax.ast.ExpressionNode

abstract class ExpressionInterpreter<T : ExpressionNode>(val interpreter: Interpreter) {

    protected val scope get() = interpreter.scope

    open fun evaluate(node: T): RuntimeValue {
        TODO("Interpreter Not Implemented")
    }

    fun evaluateOther(node: ExpressionNode): RuntimeValue = interpreter.evaluate(node)

    fun pushScope(scope: Scope) {
        interpreter.scopes.push(scope)
    }

    fun popScope() {
        interpreter.scopes.pop()
    }

}