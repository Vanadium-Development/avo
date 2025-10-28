package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

abstract class Node(open val line: Int) {
    abstract override fun toString(): String
}

abstract class ExpressionNode(
    @Transient
    override val line: Int
) : Node(line)

abstract class StatementNode(
    @Transient
    override val line: Int
) : Node(line)

data class ModuleImportNode(
    @Transient
    override val line: Int,
    val identifier: Token
) : Node(line) {
    override fun toString(): String = "Module Import Node"
}

data class ModuleNode(
    val nodes: MutableList<Node>,
    @Transient
    override val line: Int,
    val name: String,
    val imports: List<ModuleImportNode>
) : Node(line) {
    override fun toString(): String = "Module Node"
}

data class ModuleDefinitionNode(
    @Transient
    override val line: Int,
    val identifier: Token
) : Node(line) {
    override fun toString(): String = "Module Definition"
}