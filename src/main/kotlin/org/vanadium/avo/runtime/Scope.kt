package org.vanadium.avo.runtime

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.syntax.ast.BlockExpressionNode
import org.vanadium.avo.syntax.ast.FunctionDefinitionNode
import org.vanadium.avo.types.DataType

data class Scope(val parent: Scope? = null) {

    private val variables = mutableMapOf<String, Variable>()
    private val functions = mutableMapOf<String, Function>()

    /**
     * Check whether a given identifier is already used in the current scope.
     */
    private fun isIdentifierTaken(identifier: String) =
        variables.containsKey(identifier) || functions.containsKey(identifier)

    /**
     * Create a variable in the current scope.
     * This will fail if the identifier is already in use.
     */
    fun declareVariable(identifier: String, type: DataType, expression: RuntimeValue) {
        if (isIdentifierTaken(identifier)) {
            throw AvoRuntimeException("Duplicate identifier: $identifier")
        }
        variables[identifier] = Variable(this, expression, type)
    }

    /**
     * Retrieve a variable from the current scope or one of the parent scopes.
     * This will fail if the variable is not found.
     */
    fun getVariable(identifier: String): Variable {
        return (variables[identifier]
            ?: parent?.getVariable(identifier))
            ?: throw AvoRuntimeException("Undefined variable: $identifier")
    }

    /**
     * Set the value of an existing variable in the current scope or one of the parent scopes.
     * This will fail if the variable is not found.
     */
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

    /**
     * Create a copy of the current scope that references existing variables and functions.
     * This is used during a function definition to maintain lexical ordering.
     */
    fun capture(): Scope {
        val parentSnapshot = parent?.capture()
        val copy = Scope(parentSnapshot)
        variables.forEach { (identifier, variable) ->
            copy.variables[identifier] = variable
        }
        functions.forEach { (identifier, function) ->
            copy.functions[identifier] = function
        }
        return copy
    }

    /**
     * Define a function in the current scope.
     * This will fail if the identifier is already in use.
     */
    fun defineFunction(
        identifier: String?,
        signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
        returnType: DataType,
        block: BlockExpressionNode
    ): Function {
        // Anonymous Function
        if (identifier == null) {
            return Function(Scope(capture()), signature, returnType, block)
        }

        if (isIdentifierTaken(identifier)) {
            throw AvoRuntimeException("Duplicate function with identifier $identifier")
        }

        functions[identifier] = Function(this, signature, returnType, block)
        val function = Function(Scope(capture()), signature, returnType, block)
        functions[identifier] = function

        return function
    }

    /**
     * Retrieve a function. This will not find a lambda variable.
     */
    fun getFunction(identifier: String): Function {
        return functions[identifier] ?: throw AvoRuntimeException("Undefined function: $identifier")
    }

    /**
     * Retrieve a function or a lambda variable that can be invoked as a function
     */
    fun getFunctionOrLambda(identifier: String): Function {
        val function = functions[identifier]
        if (function != null) {
            return function
        }

        val variable = variables[identifier]
        if (variable != null) {
            if (variable.type !is DataType.LambdaType || variable.value !is RuntimeValue.LambdaValue)
                throw AvoRuntimeException(
                    "Cannot invoke \"$identifier\": Not a lambda variable"
                )

            return (variable.value as RuntimeValue.LambdaValue).function
        }

        val parentLambda = parent?.getFunctionOrLambda(identifier)

        if (parentLambda != null) {
            return parentLambda
        }

        throw AvoRuntimeException(
            "Cannot invoke \"$identifier\": Not a function nor lambda variable"
        )
    }
}