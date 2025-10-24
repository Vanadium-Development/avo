package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

class InstantiationNode(
    @Transient
    override val line: Int,
    val typeIdentifier: Token,
    val fields: List<InstantiationField>,
) : ExpressionNode(line) {
    data class InstantiationField(
        val identifier: Token,
        val expression: ExpressionNode
    )
}