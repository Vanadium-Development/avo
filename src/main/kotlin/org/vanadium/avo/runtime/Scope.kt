package org.vanadium.avo.runtime

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.syntax.ast.ExpressionNode

data class Scope(val parent: Scope? = null) {

    private val variables = mutableMapOf<String, ExpressionNode>()

    fun define(identifier: String, expression: ExpressionNode) {
        variables[identifier] = expression
    }

    fun get(identifier: String): ExpressionNode {
        return (variables[identifier]
            ?: parent?.get(identifier))
            ?: throw AvoRuntimeException("Undefined variable: $identifier")
    }

    fun assign(identifier: String, expression: ExpressionNode) {
        if (variables.containsKey(identifier)) {
            variables[identifier] = expression
            return
        }

        if (parent == null) {
            throw AvoRuntimeException("Undefined variable: $identifier")
        }

        parent.assign(identifier, expression)
    }

}