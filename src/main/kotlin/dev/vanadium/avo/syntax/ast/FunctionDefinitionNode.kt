package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.syntax.lexer.Token

data class FunctionDefinitionNode(
    @Transient
    override val line: Int,
    val identifier: Token,
    val anonymous: Boolean,
    val parameters: List<FunctionSignatureParameter>,
    val returnType: DataType,
    val block: BlockExpressionNode
) : ExpressionNode(line) {
    data class FunctionSignatureParameter(
        val identifier: Token,
        val type: DataType
    )

    fun noParameterCall(): ExpressionCallNode? {
        if (parameters.isNotEmpty())
            return null

        return ExpressionCallNode(
            line,
            SymbolReferenceNode(line, identifier),
            listOf()
        )
    }

    override fun toString(): String = "Function Definition"
}