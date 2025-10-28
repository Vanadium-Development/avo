package dev.vanadium.avo.syntax.ast

data class ConditionalExpressionNode(
    @Transient
    override val line: Int,
    val branches: List<ConditionalExpressionBranch>,
    val defaultBranch: BlockExpressionNode?
) : ExpressionNode(line) {
    data class ConditionalExpressionBranch(
        val condition: ExpressionNode,
        val block: BlockExpressionNode
    )

    data class ConditionalExpressionCollection(
        val branches: MutableList<ConditionalExpressionBranch>,
        var defaultBranch: BlockExpressionNode?
    )

    override fun toString(): String = "Conditional Expression"
}