package dev.vanadium.avo.syntax.ast

data class UnaryOperationNode(
    @Transient
    override val line: Int,
    val expression: ExpressionNode,
    val operation: UnaryOperationType
) : ExpressionNode(line) {
    override fun toString(): String = "Unary Opreation"
}