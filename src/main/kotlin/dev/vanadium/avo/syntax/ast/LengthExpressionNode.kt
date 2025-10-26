package dev.vanadium.avo.syntax.ast

class LengthExpressionNode(
    @Transient
    override val line: Int,
    val expression: ExpressionNode
) : ExpressionNode(line)