package dev.vanadium.avo.syntax.ast

data class ReturnStatementNode(
    @Transient
    override val line: Int,
    val expression: ExpressionNode
) : StatementNode(line)