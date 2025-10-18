package dev.vanadium.avo.syntax.ast

class ContinueStatementNode(
    @Transient
    override val line: Int
) : StatementNode(line)