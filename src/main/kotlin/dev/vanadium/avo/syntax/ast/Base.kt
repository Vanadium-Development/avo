package dev.vanadium.avo.syntax.ast

open class Node(open val line: Int)

open class ExpressionNode(
    @Transient
    override val line: Int
) : Node(line)

open class StatementNode(
    @Transient
    override val line: Int
) : Node(line)

data class ProgramNode(
    val nodes: List<Node>,
    @Transient
    override val line: Int
) : Node(line)