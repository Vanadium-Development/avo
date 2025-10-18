package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

data class VariableAssignmentNode(
    @Transient
    override val line: Int,
    val identifier: Token,
    val value: ExpressionNode
) : ExpressionNode(line)