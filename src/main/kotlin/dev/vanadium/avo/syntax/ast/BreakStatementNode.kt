package dev.vanadium.avo.syntax.ast

class BreakStatementNode(
    @Transient
    override val line: Int
) : StatementNode(line)