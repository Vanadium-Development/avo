package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

data class LoopExpressionNode(
    @Transient
    override val line: Int,
    val start: LoopBound,
    val end: LoopBound,
    val step: ExpressionNode,
    val block: BlockExpressionNode,
    val loopVariable: Token
) : ExpressionNode(line) {
    data class LoopBound(
        val expression: ExpressionNode,
        val exclusive: Boolean
    )

    override fun toString(): String = "Loop Expression"
}