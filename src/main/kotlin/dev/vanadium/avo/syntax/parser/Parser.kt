package dev.vanadium.avo.syntax.parser

import dev.vanadium.avo.exception.SyntaxException
import dev.vanadium.avo.syntax.ast.*
import dev.vanadium.avo.syntax.lexer.Lexer
import dev.vanadium.avo.syntax.lexer.Token
import dev.vanadium.avo.syntax.lexer.TokenStream
import dev.vanadium.avo.syntax.lexer.TokenType
import dev.vanadium.avo.types.DataType
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

    private fun parseLambdaType(): DataType.LambdaType {
        val signature = parseFunctionSignature(false)

        if (tokenStream.currentToken.type != TokenType.RIGHT_ARROW)
            throw SyntaxException(
                "Expected '->', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val returnType = parseDataType()

        return DataType.LambdaType(signature.map { it.type }, returnType)
    }

    private fun parseDataType(): DataType {
        val type: DataType? = when (tokenStream.currentToken.type) {
            TokenType.KW_INT -> DataType.IntegerType
            TokenType.KW_FLOAT -> DataType.FloatType
            TokenType.KW_STRING -> DataType.StringType
            TokenType.KW_BOOL -> DataType.BooleanType
            TokenType.KW_VOID -> DataType.VoidType
            TokenType.QUESTION_MARK -> DataType.InferredType
            TokenType.IDENTIFIER -> DataType.ComplexType(tokenStream.currentToken.value)
            else -> null
        }

        if (type != null) {
            tokenStream.consume()
            return type
        }

        if (tokenStream.currentToken.type == TokenType.LPAREN) {
            return parseLambdaType()
        }

        throw SyntaxException(
            "Invalid data type ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
        )
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
        var op: BinaryOperationType?
        while (BinaryOperationType.additiveFromTokenType(tokenStream.currentToken.type).also { op = it } != null) {
            tokenStream.consume()

            val expr = parseTerm()
            left = BinaryOperationNode(left, expr, op!!)
        }

        return left
    }

    private fun parseTerm(): ExpressionNode {
        var left = parseFactor()

        var op: BinaryOperationType?
        while (BinaryOperationType.multiplicativeFromTokenType(tokenStream.currentToken.type)
                .also { op = it } != null
        ) {
            tokenStream.consume()

            val expr = parseExpression()
            left = BinaryOperationNode(left, expr, op!!)
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
        var factor: ExpressionNode? = when (tokenStream.currentToken.type) {
            TokenType.FLOAT_LITERAL -> LiteralNode.FloatLiteral(tokenStream.currentToken.value.toDouble())
            TokenType.INTEGER_LITERAL -> LiteralNode.IntegerLiteral(tokenStream.currentToken.value.toInt())
            TokenType.STRING_LITERAL -> LiteralNode.StringLiteral(tokenStream.currentToken.value)
            TokenType.KW_TRUE -> LiteralNode.BooleanLiteral(true)
            TokenType.KW_FALSE -> LiteralNode.BooleanLiteral(false)
            else -> null
        }

        if (factor != null) {
            tokenStream.consume()
        }

        if (factor == null) {
            factor = when (tokenStream.currentToken.type) {
                TokenType.KW_VAR -> parseVariableDeclaration()
                TokenType.KW_IF -> parseConditionalExpression()
                TokenType.KW_FUN -> parseFunctionDefinition()
                TokenType.LPAREN -> parseSubExpression()
                else -> null
            }
        }

        if (factor == null && tokenStream.currentToken.type == TokenType.IDENTIFIER) {
            factor = when (tokenStream.nextToken.type) {
                TokenType.EQUALS -> parseVariableAssignment()
                else -> parseVariableReference()
            }
        }

        factor ?: throw SyntaxException(
            "Expected a factor, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
        )

        // Expression Call
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            return factor

        var call: ExpressionNode = factor

        // Handle a number of consecutive calls on the same expressions
        while (tokenStream.currentToken.type == TokenType.LPAREN) {
            val params = parseExpressionCallParameters()
            call = ExpressionCallNode(call, params)
        }

        return call
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

        if (tokenStream.currentToken.type != TokenType.COLON)
            throw SyntaxException(
                "Expected ':', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val type = parseDataType()

        if (tokenStream.currentToken.type != TokenType.EQUALS) {
            return VariableDeclarationNode(id, type, null)
        }

        tokenStream.consume()

        return VariableDeclarationNode(id, type, parseExpression())
    }

    private fun parseVariableAssignment(): VariableAssignmentNode {
        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxException(
                "Expected identifier, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        val id = tokenStream.currentToken

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.EQUALS)
            throw SyntaxException(
                "Expected '=', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val expr = parseExpression()

        return VariableAssignmentNode(id, expr)
    }

    private fun parseVariableReference(): SymbolReferenceNode {
        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxException(
                "Expected identifier, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        val id = tokenStream.currentToken

        tokenStream.consume()

        return SymbolReferenceNode(id)
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

    private fun parseFunctionSignature(named: Boolean): List<FunctionDefinitionNode.FunctionSignatureParameter> {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxException(
                "Expected '(', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val parameters = mutableListOf<FunctionDefinitionNode.FunctionSignatureParameter>()

        while (tokenStream.currentToken.type != TokenType.RPAREN && !tokenStream.currentToken.isEof()) {
            var paramIdentifier: Token = Token("", TokenType.IDENTIFIER, 0)

            if (named) {
                if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
                    throw SyntaxException(
                        "Expected parameter identifier, got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
                    )

                paramIdentifier = tokenStream.currentToken

                tokenStream.consume()

                if (tokenStream.currentToken.type != TokenType.COLON)
                    throw SyntaxException(
                        "Expected ':', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
                    )

                tokenStream.consume()
            }

            val paramType = parseDataType()

            parameters.add(FunctionDefinitionNode.FunctionSignatureParameter(paramIdentifier, paramType))

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RPAREN)
                    throw SyntaxException(
                        "Expected more parameters after ',', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
                    )
                tokenStream.consume()
                continue
            }
        }

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxException(
                "Reached end of file while parsing function signature"
            )

        tokenStream.consume()

        return parameters
    }

    private fun parseFunctionDefinition(): FunctionDefinitionNode {
        if (tokenStream.currentToken.type != TokenType.KW_FUN)
            throw SyntaxException(
                "Expected 'fun', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        var anon = true
        var identifier = Token.eof()

        if (tokenStream.currentToken.type == TokenType.IDENTIFIER) {
            anon = false
            identifier = tokenStream.currentToken
            tokenStream.consume()
        }

        var returnType: DataType = DataType.VoidType

        // Parse function signature
        val parameters = if (tokenStream.currentToken.type == TokenType.LPAREN) {
            parseFunctionSignature(true)
        } else {
            listOf()
        }

        // Parse return type
        if (tokenStream.currentToken.type == TokenType.RIGHT_ARROW) {
            tokenStream.consume()
            returnType = parseDataType()
        }

        val block = parseBlockExpression()

        return FunctionDefinitionNode(identifier, anon, parameters, returnType, block)
    }

    private fun parseExpressionCallParameters(): List<ExpressionCallNode.CallParameter> {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxException(
                "Expected '(', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        val parameters = mutableListOf<ExpressionCallNode.CallParameter>()

        while (tokenStream.currentToken.type != TokenType.RPAREN && !tokenStream.currentToken.isEof()) {
            val expr = parseExpression()

            parameters.add(ExpressionCallNode.CallParameter(expr))

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RPAREN)
                    throw SyntaxException(
                        "Expected more parameters after ',', got ${tokenStream.currentToken.type} on line ${tokenStream.currentToken.line}"
                    )

                continue
            }
        }

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxException(
                "Expected ')' after parameter list, got ${tokenStream.currentToken.type} Error on line ${tokenStream.currentToken.line}"
            )

        tokenStream.consume()

        return parameters
    }

}
