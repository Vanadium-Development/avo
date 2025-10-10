package org.vanadium.avo.runtime

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.syntax.ast.BlockExpressionNode
import org.vanadium.avo.syntax.ast.FunctionDefinitionNode
import org.vanadium.avo.types.DataType

data class Variable(
    val scope: Scope,
    var value: RuntimeValue,
    val type: DataType
)

data class Function(
    val scope: Scope,
    val signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
    val returnType: DataType,
    val block: BlockExpressionNode
)

data class Scope(val parent: Scope? = null) {

    private val variables = mutableMapOf<String, Variable>()
    private val functions = mutableMapOf<String, Function>()

    fun declareVariable(identifier: String, type: DataType, expression: RuntimeValue) {
        if (variables.containsKey(identifier)) {
            throw AvoRuntimeException("Duplicate variable with identifier $identifier")
        }
        variables[identifier] = Variable(this, expression, type)
    }

    fun getVariable(identifier: String): Variable {
        return (variables[identifier]
            ?: parent?.getVariable(identifier))
            ?: throw AvoRuntimeException("Undefined variable: $identifier")
    }

    fun assignVariable(identifier: String, expression: RuntimeValue) {
        if (variables.containsKey(identifier)) {
            variables[identifier]!!.value = expression
            return
        }

        if (parent == null) {
            throw AvoRuntimeException("Undefined variable: $identifier")
        }

        parent.assignVariable(identifier, expression)
    }

    fun defineFunction(
        identifier: String,
        signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
        returnType: DataType,
        block: BlockExpressionNode
    ): Function {
        if (functions.containsKey(identifier)) {
            throw AvoRuntimeException("Duplicate function with identifier $identifier")
        }

        val function = Function(this, signature, returnType, block)
        functions[identifier] = function

        return function
    }

    fun getFunction(identifier: String): Function {
        return functions[identifier] ?: throw AvoRuntimeException("Undefined function: $identifier")
    }

}