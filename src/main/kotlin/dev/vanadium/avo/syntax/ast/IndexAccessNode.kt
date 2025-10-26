package dev.vanadium.avo.syntax.ast

class IndexAccessNode(
    @Transient
    override val line: Int,
    val target: ExpressionNode,
    val index: ExpressionNode
) : ExpressionNode(line)