package org.vanadium.avo.syntax.ast

import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.syntax.lexer.Token
import org.vanadium.avo.types.DataType
import java.beans.Expression

open class Node

open class ExpressionNode : Node()

open class StatementNode : Node()

data class ProgramNode(val nodes: List<Node>) : Node()

sealed class LiteralNode : ExpressionNode() {
    abstract fun runtimeValue(): RuntimeValue

    data class IntegerLiteral(val value: Int) : LiteralNode() {
        override fun runtimeValue(): RuntimeValue {
            return RuntimeValue.IntegerValue(value)
        }
    }
    data class FloatLiteral(val value: Double) : LiteralNode() {
        override fun runtimeValue(): RuntimeValue {
            return RuntimeValue.FloatValue(value)
        }
    }
    data class StringLiteral(val value: String) : LiteralNode() {
        override fun runtimeValue(): RuntimeValue {
            return RuntimeValue.StringValue(value)
        }
    }
    data class BooleanLiteral(val value: Boolean) : LiteralNode() {
        override fun runtimeValue(): RuntimeValue {
            return RuntimeValue.BooleanValue(value)
        }
    }
}

data class BinaryOperationNode(val left: ExpressionNode, val right: ExpressionNode, val type: BinaryOperationType) :
    ExpressionNode()

data class UnaryOperationNode(val expression: ExpressionNode, val operation: UnaryOperationType) : ExpressionNode()

data class BlockExpressionNode(val nodes: List<Node>, val parent: BlockExpressionNode?)

data class ConditionalExpressionNode(
    val branches: List<ConditionalExpressionBranch>,
    val defaultBranch: BlockExpressionNode?
) : ExpressionNode() {
    data class ConditionalExpressionBranch(
        val condition: ExpressionNode,
        val block: BlockExpressionNode
    )

    data class ConditionalExpressionCollection(
        val branches: MutableList<ConditionalExpressionBranch>,
        var defaultBranch: BlockExpressionNode?
    )
}

data class FunctionDefinitionNode(val identifier: Token, val parameters: List<FunctionSignatureParameter>, val returnType: DataType, val block: BlockExpressionNode) : ExpressionNode() {
    data class FunctionSignatureParameter(val identifier: Token, val type: DataType)
}

data class FunctionCallNode(val identifier: Token, val parameters: List<FunctionCallParameter>): ExpressionNode() {
    data class FunctionCallParameter(val expression: ExpressionNode)
}

data class VariableDeclarationNode(val identifier: Token, val type: DataType, val value: ExpressionNode?) :
    ExpressionNode()

data class VariableReferenceNode(val identifier: Token) : ExpressionNode()

data class ReturnStatementNode(val expression: ExpressionNode) : StatementNode()

class ContinueStatementNode : StatementNode()

class BreakStatementNode : StatementNode()

