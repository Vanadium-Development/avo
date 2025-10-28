package dev.vanadium.avo.syntax.ast

data class BlockExpressionNode(
    @Transient
    override val line: Int,
    val nodes: List<Node>,
    @Transient
    val parent: BlockExpressionNode?
) : ExpressionNode(line) {
    override fun toString(): String = "Block Expression"
}