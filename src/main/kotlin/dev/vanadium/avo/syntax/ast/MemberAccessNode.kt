package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

data class MemberAccessNode(
    override val line: Int,
    val receiver: ExpressionNode,
    val member: Token
) : ExpressionNode(line) {
    override fun toString(): String = "Member Access"
}