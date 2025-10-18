package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

data class SymbolReferenceNode(
    @Transient
    override val line: Int,
    val identifier: Token
) : ExpressionNode(line)