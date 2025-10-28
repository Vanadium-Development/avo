package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

data class InternalFunctionCallNode(
    @Transient
    override val line: Int,
    val identifier: Token,
    val parameters: List<ExpressionCallNode.CallParameter>
) : ExpressionNode(line) {
    override fun toString(): String = "Internal Call"
}