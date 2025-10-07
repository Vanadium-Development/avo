package org.vanadium.avo.syntax.parser

import org.vanadium.avo.exception.SyntaxException
import org.vanadium.avo.syntax.ast.*
import org.vanadium.avo.syntax.lexer.Lexer
import org.vanadium.avo.syntax.lexer.Token
import org.vanadium.avo.syntax.lexer.TokenType

class Parser(val lexer: Lexer) {

    private var tokenCurrent: Token = Token.eof()
    private var tokenNext: Token = Token.eof()

    init {
        nextToken()
        nextToken()
    }

    private fun nextToken() {
        tokenCurrent = tokenNext
        tokenNext = lexer.nextToken()
    }

    fun parse(): ProgramNode {
        val nodes = mutableListOf<Node>()
        while (tokenCurrent.type != TokenType.EOF) {
            nodes.add(parseExpression())
        }
        return ProgramNode(nodes)
    }

    private fun parseVariableDeclaration(): VariableDeclarationNode {
        if (tokenCurrent.type != TokenType.KW_VAR)
            throw SyntaxException("Expected 'var' at the beginning of a variable declaration, got ${tokenCurrent.type} on line ${tokenCurrent.line}")

        nextToken()

        if (tokenCurrent.type != TokenType.IDENTIFIER)
            throw SyntaxException("Expected variable identifier, got ${tokenCurrent.type} on line ${tokenCurrent.line}")

        val id = tokenCurrent

        nextToken()

        if (tokenCurrent.type != TokenType.EQUALS)
            throw SyntaxException("Expected '=', got ${tokenCurrent.type} on line ${tokenCurrent.line}")

        nextToken()

        return VariableDeclarationNode(id, parseExpression())
    }

    private fun parseExpression(): ExpressionNode {
        var left: ExpressionNode = parseTerm()
        while (tokenCurrent.type.isAdditiveOperation()) {
            val op = BinaryOperationType.fromTokenType(tokenCurrent.type)
                ?: throw SyntaxException("Unknown operation type ${tokenCurrent.type} on line ${tokenCurrent.line}")

            nextToken()

            val expr = parseTerm()
            left = BinaryOperationNode(left, expr, op)
        }

        return left
    }

    private fun parseTerm(): ExpressionNode {
        var left: ExpressionNode = when (tokenCurrent.type) {
            TokenType.LPAREN -> parseSubExpression()
            else -> parseFactor()
        }
        while (tokenCurrent.type.isMultiplicativeOperation()) {
            val op = BinaryOperationType.fromTokenType(tokenCurrent.type)
                ?: throw SyntaxException("Unknown operation type ${tokenCurrent.type} on line ${tokenCurrent.line}")

            nextToken()

            val expr = parseExpression()
            left = BinaryOperationNode(left, expr, op)
        }
        return left
    }

    private fun parseSubExpression(): ExpressionNode {
        if (tokenCurrent.type != TokenType.LPAREN)
            throw SyntaxException("Expected '(' at the start of a sub-expression, got ${tokenCurrent.type} on line ${tokenCurrent.line}")

        nextToken()

        val expr = parseExpression()

        if (tokenCurrent.type != TokenType.RPAREN)
            throw SyntaxException("Expected ')' after a sub-expression, got ${tokenCurrent.type} on line ${tokenCurrent.line}")

        nextToken()

        return expr
    }

    private fun parseFactor(): ExpressionNode {
        val literal = when (tokenCurrent.type) {
            TokenType.FLOAT_LITERAL -> LiteralNode.FloatLiteral(tokenCurrent.value.toDouble())
            TokenType.INTEGER_LITERAL -> LiteralNode.IntegerLiteral(tokenCurrent.value.toInt())
            TokenType.STRING_LITERAL -> LiteralNode.StringLiteral(tokenCurrent.value)
            TokenType.KW_VAR -> parseVariableDeclaration()
            else -> throw SyntaxException("Expected a literal, got ${tokenCurrent.type} on line ${tokenCurrent.line}")
        }
        nextToken()
        return literal
    }
}
