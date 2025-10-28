package dev.vanadium.avo.syntax.ast

data class AssignmentNode(
    @Transient
    override val line: Int,
    val target: ExpressionNode,
    val value: ExpressionNode
) : ExpressionNode(line) {
    override fun toString(): String = "Assignment"
}