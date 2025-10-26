package dev.vanadium.avo.runtime

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.types.ComplexType
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.runtime.types.symbol.Function
import dev.vanadium.avo.runtime.types.symbol.Symbol
import dev.vanadium.avo.runtime.types.symbol.Variable
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.syntax.ast.BlockExpressionNode
import dev.vanadium.avo.syntax.ast.ComplexTypeDefinitionNode
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode
import dev.vanadium.avo.util.findFirstDuplicate
import dev.vanadium.avo.util.ifPresent

data class Scope(
    @Transient
    val parent: Scope? = null
) {

    private val symbols = mutableMapOf<String, Symbol>()

    private val complexTypes = mutableMapOf<String, ComplexType>()

    /**
     * Check whether a given symbol identifier is already used in the current scope.
     */
    private fun isSymbolIdentifierTaken(identifier: String) =
        symbols.containsKey(identifier)

    /**
     * Check whether a given complex type  identifier is already used in the current scope.
     */
    private fun isComplexTypeIdentifierTaken(identifier: String) =
        complexTypes.containsKey(identifier)

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
        if (isSymbolIdentifierTaken(identifier)) {
            throw RuntimeError(
                "Duplicate identifier: $identifier",
                line
            )
        }

        listOf(type).validateTypes(line)

        symbols[identifier] = Variable(this, expression, type)
    }

    /**
     * Set the value of an existing variable in this or one of the parent scopes.
     * This will fail if the variable is not found.
     */
    fun assignVariable(
        identifier: String,
        expression: RuntimeValue,
        line: Int,
    ) {
        if (symbols.containsKey(identifier)) {
            if (symbols[identifier] !is Variable)
                throw RuntimeError(
                    "Symbol is not a variable: $identifier",
                    line
                )

            (symbols[identifier] as Variable).value = expression
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
     * Create a copy of this scope that references existing variables and functions.
     * This is used during a function definition to maintain lexical ordering.
     */
    fun capture(): Scope {
        val parentSnapshot = parent?.capture()
        val copy = Scope(parentSnapshot)
        symbols.forEach { (identifier, variable) ->
            copy.symbols[identifier] = variable
        }
        complexTypes.forEach { (identifier, complexType) ->
            copy.complexTypes[identifier] = complexType
        }
        return copy
    }

    /**
     * Define a function in this scope.
     * This will fail if the identifier is already in use.
     */
    fun defineFunction(
        identifier: String?,
        signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
        returnType: DataType,
        block: BlockExpressionNode,
        line: Int
    ): Function {
        val formattedIdentifier = if (identifier == null) "<anonymous>" else "\"$identifier\""

        signature.map { it.identifier.value }.findFirstDuplicate() ifPresent {
            throw RuntimeError(
                "Duplicate parameter identifier \"$it\" in signature of function $formattedIdentifier",
                line
            )
        }

        listOf(*signature.map { it.type }.toTypedArray(), returnType).validateTypes(line)

        // Anonymous Function
        if (identifier == null) {
            return Function(Scope(capture()), identifier, signature, returnType, block)
        }

        if (isSymbolIdentifierTaken(identifier)) {
            throw RuntimeError(
                "Duplicate function with identifier $identifier",
                line
            )
        }

        symbols[identifier] = Function(this, identifier, signature, returnType, block)
        val function = Function(Scope(capture()), identifier, signature, returnType, block)
        symbols[identifier] = function

        return function
    }

    /**
     * Get a symbol from this or one of the parent scopes.
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


    /**
     * Define a complex type in this scope.
     * This will fail if the identifier is already in use.
     */
    fun defineComplexType(
        identifier: String,
        fields: List<ComplexTypeDefinitionNode.ComplexTypeField>,
        line: Int
    ) {
        if (isComplexTypeIdentifierTaken(identifier))
            throw RuntimeError(
                "Duplicate type identifier: $identifier",
                line
            )

        fields.map { it.identifier.value }.findFirstDuplicate() ifPresent {
            throw RuntimeError(
                "Duplicate field \"$it\" in definition of complex type \"$identifier\"",
                line
            )
        }

        fields.map { it.dataType }.validateTypes(line)

        complexTypes[identifier] = ComplexType(
            identifier,
            fields,
            line
        )
    }

    /**
     * Retrieve a complex type from this or one of the parent scopes.
     * This will fail if the type does not exist.
     */
    fun getComplexType(
        identifier: String,
        line: Int
    ): ComplexType {
        return getComplexTypeOrNull(identifier) ?: throw RuntimeError(
            "Undefined complex type: $identifier",
            line
        )
    }

    /**
     * Retrieve a complex type from this or one of the parent scopes
     * or `null` if the type does not exist.
     */
    fun getComplexTypeOrNull(
        identifier: String,
    ): ComplexType? {
        val type = complexTypes[identifier]
        if (type != null)
            return type
        if (parent == null)
            return null
        return parent.getComplexTypeOrNull(identifier)
    }

    /**
     * Throws a `RuntimeError` if a Type in This List is Undefined
     */
    fun List<DataType>.validateTypes(line: Int): Boolean {
        forEach { type ->
            if (type !is DataType.ComplexTypeReferenceNode)
                return@forEach

            if (getComplexTypeOrNull(type.identifier) == null)
                throw RuntimeError(
                    "Undefined type \"${type.identifier}\" on line $line",
                    line
                )
        }

        return true
    }
}