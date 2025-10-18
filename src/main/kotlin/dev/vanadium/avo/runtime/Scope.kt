package dev.vanadium.avo.runtime

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.types.DataType
import dev.vanadium.avo.runtime.interpreter.types.value.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.types.Symbol
import dev.vanadium.avo.syntax.ast.BlockExpressionNode
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

data class Scope(
    @Transient
    val parent: Scope? = null
) {

    private val symbols = mutableMapOf<String, Symbol>()

    /**
     * Check whether a given identifier is already used in the current scope.
     */
    private fun isIdentifierTaken(identifier: String) =
        symbols.containsKey(identifier)

    /**
     * Create a variable in the current scope.
     * This will fail if the identifier is already in use.
     */
    fun declareVariable(
        identifier: String,
        type: DataType,
        expression: RuntimeValue,
        line: Int
    ) {
        if (isIdentifierTaken(identifier)) {
            throw RuntimeError(
                "Duplicate identifier: $identifier",
                line
            )
        }
        symbols[identifier] = Symbol.Variable(this, expression, type)
    }

    /**
     * Set the value of an existing variable in the current scope or one of the parent scopes.
     * This will fail if the variable is not found.
     */
    fun assignVariable(
        identifier: String,
        expression: RuntimeValue,
        line: Int,
    ) {
        if (symbols.containsKey(identifier)) {
            if (symbols[identifier] !is Symbol.Variable)
                throw RuntimeError(
                    "Symbol is not a variable: $identifier",
                    line
                )

            (symbols[identifier] as Symbol.Variable).value = expression
            return
        }

        if (parent == null) {
            throw RuntimeError(
                "Undefined variable: $identifier",
                line
            )
        }

        parent.assignVariable(identifier, expression, line)
    }

    /**
     * Create a copy of the current scope that references existing variables and functions.
     * This is used during a function definition to maintain lexical ordering.
     */
    fun capture(): Scope {
        val parentSnapshot = parent?.capture()
        val copy = Scope(parentSnapshot)
        symbols.forEach { (identifier, variable) ->
            copy.symbols[identifier] = variable
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
        block: BlockExpressionNode,
        line: Int
    ): Symbol.Function {
        // Anonymous Function
        if (identifier == null) {
            return Symbol.Function(Scope(capture()), identifier, signature, returnType, block)
        }

        if (isIdentifierTaken(identifier)) {
            throw RuntimeError(
                "Duplicate function with identifier $identifier",
                line
            )
        }

        symbols[identifier] = Symbol.Function(this, identifier, signature, returnType, block)
        val function = Symbol.Function(Scope(capture()), identifier, signature, returnType, block)
        symbols[identifier] = function

        return function
    }

    /**
     * Get a symbol from the current or the parent scopes.
     * This will fail if the symbol cannot be found.
     */
    fun getSymbol(
        identifier: String,
        line: Int
    ): Symbol {
        val symbol = symbols[identifier]
        if (symbol != null) {
            return symbol
        }

        if (parent == null)
            throw RuntimeError(
                "Undefined symbol: $identifier",
                line
            )

        return parent.getSymbol(identifier, line)
    }
}