package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.syntax.lexer.Token
import dev.vanadium.avo.types.DataType

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

data class BlockExpressionNode(
    val nodes: List<Node>,

    @Transient
    val parent: BlockExpressionNode?
) : ExpressionNode()

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

data class FunctionDefinitionNode(
    val identifier: Token,
    val anonymous: Boolean,
    val parameters: List<FunctionSignatureParameter>,
    val returnType: DataType,
    val block: BlockExpressionNode
) : ExpressionNode() {
    data class FunctionSignatureParameter(val identifier: Token, val type: DataType)
}

data class ExpressionCallNode(val expression: ExpressionNode, val parameters: List<CallParameter>) : ExpressionNode() {
    data class CallParameter(val expression: ExpressionNode)
}

data class VariableDeclarationNode(val identifier: Token, var type: DataType, val value: ExpressionNode?) :
    ExpressionNode()

data class VariableAssignmentNode(val identifier: Token, val value: ExpressionNode) : ExpressionNode()

data class SymbolReferenceNode(val identifier: Token) : ExpressionNode()

data class ReturnStatementNode(val expression: ExpressionNode) : StatementNode()

class ContinueStatementNode : StatementNode()

class BreakStatementNode : StatementNode()

class InternalFunctionDefinitionNode(
    val classPath: Token,
    val identifier: Token,
    val signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
    val returnType: DataType
) : ExpressionNode()