package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.syntax.lexer.Token

open class Node(open val line: Int)

open class ExpressionNode(
    @Transient
    override val line: Int
) : Node(line)

open class StatementNode(
    @Transient
    override val line: Int
) : Node(line)

data class ModuleImportNode(
    @Transient
    override val line: Int,
    val identifier: Token
) : Node(line)

data class ModuleNode(
    val nodes: List<Node>,
    @Transient
    override val line: Int,
    val name: String,
    val imports: List<ModuleImportNode>
) : Node(line)

data class ModuleDefinitionNode(
    @Transient
    override val line: Int,
    val identifier: Token
) : Node(line)