package org.vanadium.avo.syntax.ast

open class Node

open class ExpressionNode : Node()

data class ProgramNode(val nodes: List<ExpressionNode>) : Node()

sealed class LiteralNode : ExpressionNode() {
    data class IntegerLiteral(val value: Int) : LiteralNode()
    data class FloatLiteral(val value: Double) : LiteralNode()
    data class StringLiteral(val value: String) : LiteralNode()
}

data class BinaryOperationNode(val left: ExpressionNode, val right: ExpressionNode, val type: BinaryOperationType) :
    ExpressionNode()

data class UnaryOperationNode(val expression: ExpressionNode, val operation: UnaryOperationType) : ExpressionNode()
