package dev.vanadium.avo.syntax.ast

class ArrayLiteralNode(
    @Transient
    override val line: Int,
    val values: List<ExpressionNode>
) : ExpressionNode(line)