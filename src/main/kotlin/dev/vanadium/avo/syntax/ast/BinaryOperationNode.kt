package dev.vanadium.avo.syntax.ast

data class BinaryOperationNode(
    @Transient
    override val line: Int,
    val left: ExpressionNode,
    val right: ExpressionNode,
    val type: BinaryOperationType
) : ExpressionNode(line) {
    override fun toString(): String = "Binary Operation"
}