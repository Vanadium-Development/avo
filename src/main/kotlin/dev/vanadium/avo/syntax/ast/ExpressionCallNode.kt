package dev.vanadium.avo.syntax.ast

data class ExpressionCallNode(
    @Transient
    override val line: Int,
    val expression: ExpressionNode,
    val parameters: List<CallParameter>
) : ExpressionNode(line) {
    data class CallParameter(val expression: ExpressionNode)
}