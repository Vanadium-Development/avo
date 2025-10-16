package org.vanadium.avo.runtime.interpreter

import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.Scope
import org.vanadium.avo.syntax.ast.ExpressionNode

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