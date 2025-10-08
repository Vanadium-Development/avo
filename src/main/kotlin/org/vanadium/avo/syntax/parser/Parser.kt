package org.vanadium.avo.syntax.parser

import org.vanadium.avo.exception.SyntaxException
import org.vanadium.avo.syntax.ast.*
import org.vanadium.avo.syntax.lexer.Lexer
import org.vanadium.avo.syntax.lexer.TokenStream
import org.vanadium.avo.syntax.lexer.TokenType
import java.util.*

class Parser(lexer: Lexer) {

    private val tokenStream = TokenStream(lexer)
    private val blockHierarchy = Stack<BlockExpressionNode>()

    fun parse(): ProgramNode {
        val nodes = mutableListOf<Node>()
        while (tokenStream.currentToken.type != TokenType.EOF) {
            nodes.add(parseExpression())
        }
        return ProgramNode(nodes)
    }

    private fun parseStatement(): StatementNode? = when (tokenStream.currentToken.type) {
        TokenType.KW_RETURN -> parseReturnStatement()
        TokenType.KW_CONTINUE -> parseContinueStatement()
        TokenType.KW_BREAK -> parseBreakStatement()
        else -> null
    }

    private fun parseReturnStatement(): ReturnStatementNode {
        if (tokenStream.currentToken.type != TokenType.KW_RETURN)
            throw SyntaxException(
                "Expected 'return' on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val expr = parseExpression()

        return ReturnStatementNode(expr)
    }

    private fun parseContinueStatement(): ContinueStatementNode {
        if (tokenStream.currentToken.type != TokenType.KW_CONTINUE)
            throw SyntaxException(
                "Expected 'continue' on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        return ContinueStatementNode()
    }

    private fun parseBreakStatement(): BreakStatementNode {
        if (tokenStream.currentToken.type != TokenType.KW_BREAK)
            throw SyntaxException(
                "Expected 'break' on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        return BreakStatementNode()
    }

    private fun parseExpression(): ExpressionNode {
        var left: ExpressionNode = parseTerm()
        while (tokenStream.currentToken.type.isAdditiveOperation()) {
            val op = BinaryOperationType.fromTokenType(tokenStream.currentToken.type)
                ?: throw SyntaxException("Unknown operation type ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}")

            tokenStream.consume()

            val expr = parseTerm()
            left = BinaryOperationNode(left, expr, op)
        }

        return left
    }

    private fun parseTerm(): ExpressionNode {
        var left: ExpressionNode = when (tokenStream.currentToken.type) {
            TokenType.LPAREN -> parseSubExpression()
            else -> parseFactor()
        }
        while (tokenStream.currentToken.type.isMultiplicativeOperation()) {
            val op = BinaryOperationType.fromTokenType(tokenStream.currentToken.type)
                ?: throw SyntaxException("Unknown operation type ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}")

            tokenStream.consume()

            val expr = parseExpression()
            left = BinaryOperationNode(left, expr, op)
        }
        return left
    }

    private fun parseSubExpression(): ExpressionNode {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxException("Expected '(' at the start of a sub-expression, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}")

        tokenStream.consume()

        val expr = parseExpression()

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxException("Expected ')' after a sub-expression, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}")

        tokenStream.consume()

        return expr
    }

    private fun parseFactor(): ExpressionNode {
        var literal: ExpressionNode? = when (tokenStream.currentToken.type) {
            TokenType.FLOAT_LITERAL -> LiteralNode.FloatLiteral(tokenStream.currentToken.value.toDouble())
            TokenType.INTEGER_LITERAL -> LiteralNode.IntegerLiteral(tokenStream.currentToken.value.toInt())
            TokenType.STRING_LITERAL -> LiteralNode.StringLiteral(tokenStream.currentToken.value)
            else -> null
        }

        if (literal != null) {
            tokenStream.consume()
            return literal
        }

        literal = when (tokenStream.currentToken.type) {
            TokenType.KW_VAR -> parseVariableDeclaration()
            TokenType.KW_IF -> parseConditionalExpression()
            else -> throw SyntaxException(
                "Expected a factor, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )
        }

        return literal
    }

    private fun parseVariableDeclaration(): VariableDeclarationNode {
        if (tokenStream.currentToken.type != TokenType.KW_VAR)
            throw SyntaxException(
                "Expected 'var' at the beginning of a variable declaration, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxException(
                "Expected variable identifier, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        val id = tokenStream.currentToken

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.EQUALS)
            throw SyntaxException(
                "Expected '=', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        return VariableDeclarationNode(id, parseExpression())
    }

    private fun parseBlockExpression(): BlockExpressionNode {
        val line = tokenStream.currentToken.line

        if (tokenStream.currentToken.type != TokenType.LBRACE)
            throw SyntaxException(
                "Expected '{' at the start of a block expression, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val nodes = mutableListOf<Node>()
        val parent = if (blockHierarchy.isEmpty()) null else blockHierarchy.peek()
        val block = BlockExpressionNode(nodes, parent)
        blockHierarchy.push(block)

        while (tokenStream.currentToken.type != TokenType.RBRACE && !tokenStream.currentToken.isEof()) {
            val statement = parseStatement()
            if (statement != null) {
                nodes.add(statement)
                continue
            }
            nodes.add(parseExpression())
        }

        blockHierarchy.pop()

        if (tokenStream.currentToken.type != TokenType.RBRACE)
            throw SyntaxException(
                "Reached end of file while parsing block starting on line $line"
            )

        tokenStream.consume()

        return BlockExpressionNode(nodes, parent)
    }

    private fun parseConditionalExpressionBranch(): ConditionalExpressionNode.ConditionalExpressionCollection? {
        if (tokenStream.currentToken.type != TokenType.KW_IF)
            throw SyntaxException("Expected 'if' at the start of a conditional expression, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}")

        tokenStream.consume()

        val expr = parseExpression()
        val block = parseBlockExpression()

        val collectBranch = ConditionalExpressionNode.ConditionalExpressionCollection(
            mutableListOf(
                ConditionalExpressionNode.ConditionalExpressionBranch(expr, block)
            ),
            null
        )

        if (tokenStream.currentToken.type == TokenType.KW_ELSE && tokenStream.nextToken.type == TokenType.KW_IF) {
            tokenStream.consume()
            val subTree = parseConditionalExpressionBranch()
            if (subTree != null) {
                collectBranch.branches.addAll(subTree.branches)
                collectBranch.defaultBranch = subTree.defaultBranch
            }
        } else if (tokenStream.currentToken.type == TokenType.KW_ELSE) {
            if (collectBranch.defaultBranch != null)
                throw SyntaxException(
                    "A conditional expression cannot have more than one default branch. Conflicting branch defined on line ${tokenStream.currentToken.line}"
                )

            tokenStream.consume()

            val block = parseBlockExpression()
            collectBranch.defaultBranch = block
        }

        return collectBranch
    }

    private fun parseConditionalExpression(): ConditionalExpressionNode {
        val collection = parseConditionalExpressionBranch() ?: throw SyntaxException(
            "Conditional expression tree does not exist in conditional expression. Error on line ${tokenStream.currentToken.line}"
        )
        return ConditionalExpressionNode(collection.branches, collection.defaultBranch)
    }
}
