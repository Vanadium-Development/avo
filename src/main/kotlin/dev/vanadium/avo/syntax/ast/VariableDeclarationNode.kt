package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.syntax.lexer.Token

data class VariableDeclarationNode(
    @Transient
    override val line: Int,
    val identifier: Token,
    var type: DataType,
    val value: ExpressionNode?
) : ExpressionNode(line) {
    override fun toString(): String = "Variable Declaration"
}