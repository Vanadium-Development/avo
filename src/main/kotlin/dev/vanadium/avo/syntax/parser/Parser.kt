package dev.vanadium.avo.syntax.parser

import dev.vanadium.avo.error.SyntaxError
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.syntax.ast.*
import dev.vanadium.avo.syntax.lexer.Lexer
import dev.vanadium.avo.syntax.lexer.Token
import dev.vanadium.avo.syntax.lexer.TokenStream
import dev.vanadium.avo.syntax.lexer.TokenType
import java.util.*

class Parser(lexer: Lexer) {
    private val tokenStream = TokenStream(lexer)
    private val currentLine get() = tokenStream.currentToken.line
    private val blockHierarchy = Stack<BlockExpressionNode>()

    fun parse(): ModuleNode {
        val nodes = mutableListOf<Node>()

        var moduleDefinition: ModuleDefinitionNode? = null
        val imports = mutableListOf<ModuleImportNode>()

        while (!tokenStream.currentToken.isEof()) {
            val node = parseAny()

            if (node !is FunctionDefinitionNode && node !is VariableDeclarationNode && node !is ModuleDefinitionNode
                && node !is ModuleImportNode && node !is ComplexTypeDefinitionNode)
                throw SyntaxError(
                    "Unexpected $node",
                    currentLine
                )

            // Module definition (must be at the top of the source)
            if (node is ModuleDefinitionNode) {
                if (moduleDefinition != null)
                    throw SyntaxError(
                        "Module ${moduleDefinition.identifier.asIdentifier()} is already defined on line ${moduleDefinition.line}. " +
                        "Attempted re-definition on line ${node.line}",
                        node.line
                    )

                if (nodes.isNotEmpty())
                    throw SyntaxError(
                        "Module definition must be at the top of the file",
                        node.line
                    )

                moduleDefinition = node
                continue
            }

            // Imports must directly follow the module definition
            if (node is ModuleImportNode) {
                if (nodes.isNotEmpty())
                    throw SyntaxError(
                        "Module import must be placed after the module definition",
                        node.line
                    )

                imports.add(node)
                continue
            }
            nodes.add(node)
        }

        if (moduleDefinition == null)
            throw SyntaxError(
                "A module definition must be present at the top of the file",
                currentLine
            )

        return ModuleNode(nodes, 1, moduleDefinition.identifier.value, imports)
    }

    private fun parseAny(): Node {
        var n: Node? = when (tokenStream.currentToken.type) {
            TokenType.KW_RETURN   -> parseReturnStatement()
            TokenType.KW_CONTINUE -> parseContinueStatement()
            TokenType.KW_BREAK    -> parseBreakStatement()
            TokenType.KW_COMPLEX  -> parseComplexTypeDefinition()
            TokenType.KW_MODULE   -> parseModuleDefinition()
            TokenType.KW_IMPORT   -> parseModuleImport()
            else                  -> null
        }

        if (n == null)
            n = parseExpression()

        while (tokenStream.currentToken.type == TokenType.SEMICOLON)
            tokenStream.consume()

        return n
    }

    private fun parseModuleDefinition(): ModuleDefinitionNode {
        if (tokenStream.currentToken.type != TokenType.KW_MODULE)
            throw SyntaxError(
                "Expected 'module' keyword at the start of a module declaration, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected module identifier after 'module' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val identifier = tokenStream.currentToken

        tokenStream.consume()

        return ModuleDefinitionNode(line, identifier)
    }

    private fun parseModuleImport(): ModuleImportNode {
        if (tokenStream.currentToken.type != TokenType.KW_IMPORT)
            throw SyntaxError(
                "Expected 'import' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected identifier of module to import, got ${tokenStream.currentToken}",
                currentLine
            )

        val identifier = tokenStream.currentToken

        tokenStream.consume()

        return ModuleImportNode(line, identifier)
    }

    private fun parseLambdaType(): DataType.LambdaType {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxError(
                "Expected '(' at the start of a lambda type, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val signature = parseFunctionSignature(false)

        if (tokenStream.currentToken.type != TokenType.RIGHT_ARROW)
            throw SyntaxError(
                "Expected '->' after lambda type signature , got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val returnType = parseDataType()

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxError(
                "Expected ')' at the end of a lambda type, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return DataType.LambdaType(signature.map { it.type }, returnType)
    }

    private fun parseArrayType(): DataType.ArrayType {
        if (tokenStream.currentToken.type != TokenType.LBRACKET)
            throw SyntaxError(
                "Expected '[' at the start of an array type, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val elementType = parseDataType()

        if (tokenStream.currentToken.type != TokenType.RBRACKET)
            throw SyntaxError(
                "Expected ']' after array element type, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return DataType.ArrayType(elementType)
    }

    private fun parseDataType(): DataType {
        val type: DataType? = when (tokenStream.currentToken.type) {
            TokenType.KW_INT        -> DataType.IntegerType
            TokenType.KW_FLOAT      -> DataType.FloatType
            TokenType.KW_STRING     -> DataType.StringType
            TokenType.KW_BOOL       -> DataType.BooleanType
            TokenType.KW_VOID       -> DataType.VoidType
            TokenType.QUESTION_MARK -> DataType.InferredType
            TokenType.IDENTIFIER    -> DataType.ComplexTypeReferenceNode(tokenStream.currentToken.value)
            else                    -> null
        }

        if (type != null) {
            tokenStream.consume()
            return type
        }

        if (tokenStream.currentToken.type == TokenType.LPAREN)
            return parseLambdaType()

        if (tokenStream.currentToken.type == TokenType.LBRACKET)
            return parseArrayType()

        throw SyntaxError(
            "Invalid data type starting with ${tokenStream.currentToken}",
            currentLine
        )
    }

    private fun parseComplexTypeDefinition(): ComplexTypeDefinitionNode {
        if (tokenStream.currentToken.type != TokenType.KW_COMPLEX)
            throw SyntaxError(
                "Expected 'complex' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected complex type identifier, got ${tokenStream.currentToken}",
                currentLine
            )

        val id = tokenStream.currentToken

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.LBRACE)
            throw SyntaxError(
                "Expected '{' after complex type identifier ${id.asIdentifier()}, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val fields = mutableListOf<ComplexTypeDefinitionNode.ComplexTypeField>()

        // Parse kind fields
        while (tokenStream.currentToken.type != TokenType.RBRACE && !tokenStream.currentToken.isEof()) {
            if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
                throw SyntaxError(
                    "Expected field identifier for complex type ${id.asIdentifier()}, got ${tokenStream.currentToken}",
                    currentLine
                )

            val fieldIdentifier = tokenStream.currentToken

            tokenStream.consume()

            if (tokenStream.currentToken.type != TokenType.COLON)
                throw SyntaxError(
                    "Expected ':' after field identifier ${fieldIdentifier.asIdentifier()} of complex type ${id.asIdentifier()}, got ${tokenStream.currentToken}",
                    currentLine
                )

            tokenStream.consume()

            val type = parseDataType()
            fields.add(
                ComplexTypeDefinitionNode.ComplexTypeField(
                    fieldIdentifier,
                    type
                )
            )

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RBRACE)
                    throw SyntaxError(
                        "Expected complex type field after ',', got ${tokenStream.nextToken}",
                        currentLine
                    )

                tokenStream.consume()
                continue
            }

            break
        }

        if (tokenStream.currentToken.type != TokenType.RBRACE)
            throw SyntaxError(
                "Expected '}' after field list, or ',' and more fields of complex type ${id.asIdentifier()}",
                currentLine
            )

        if (fields.isEmpty())
            throw SyntaxError(
                "Complex type ${id.asIdentifier()} must be declared with at least one field",
                line
            )

        tokenStream.consume()

        return ComplexTypeDefinitionNode(line, id, fields)
    }

    private fun parseComplexTypeInstantiation(): InstantiationNode {
        if (tokenStream.currentToken.type != TokenType.KW_NEW)
            throw SyntaxError(
                "Expected 'new' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        val typeLine = currentLine
        val type = parseDataType()

        if (type !is DataType.ComplexTypeReferenceNode) {
            throw SyntaxError(
                "Expected complex type identifier after 'new' keyword, got $type",
                typeLine
            )
        }

        val typeIdentifier = Token(type.identifier, TokenType.IDENTIFIER, typeLine)

        if (tokenStream.currentToken.type != TokenType.LBRACE)
            throw SyntaxError(
                "Expected '{', got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val fields = mutableListOf<InstantiationNode.InstantiationField>()

        while (tokenStream.currentToken.type != TokenType.RBRACE && !tokenStream.currentToken.isEof()) {
            if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
                throw SyntaxError(
                    "Expected field identifier, got ${tokenStream.currentToken}",
                    currentLine
                )

            val fieldId = tokenStream.currentToken

            tokenStream.consume()

            if (tokenStream.currentToken.type != TokenType.EQUALS)
                throw SyntaxError(
                    "Expected '=' after identifier of field ${fieldId.asIdentifier()}, got ${tokenStream.currentToken}",
                    currentLine
                )

            tokenStream.consume()

            val fieldExpr = parseExpression()

            fields.add(
                InstantiationNode.InstantiationField(
                    fieldId,
                    fieldExpr
                )
            )

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RBRACE)
                    throw SyntaxError(
                        "Expected instance field after ',', got ${tokenStream.nextToken}",
                        currentLine
                    )

                tokenStream.consume()
                continue
            }

            break
        }

        if (tokenStream.currentToken.type != TokenType.RBRACE)
            throw SyntaxError(
                "Expected '}' after instance field list, or ',' and more fields of ${type.identifier}, got ${tokenStream.currentToken}",
                currentLine
            )

        if (fields.isEmpty())
            throw SyntaxError(
                "Complex type ${type.identifier} must be instantiated with at least one field",
                currentLine
            )

        tokenStream.consume()

        return InstantiationNode(
            line,
            typeIdentifier,
            fields
        )
    }

    private fun parseArrayLiteral(): ArrayLiteralNode {
        if (tokenStream.currentToken.type != TokenType.LBRACKET)
            throw SyntaxError(
                "Expected '[' at the start of an array literal, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        val values = mutableListOf<ExpressionNode>()

        while (tokenStream.currentToken.type != TokenType.RBRACKET && !tokenStream.currentToken.isEof()) {
            val expr = parseExpression()

            values.add(expr)

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RBRACKET)
                    throw SyntaxError(
                        "Expected array element after ',', got ${tokenStream.nextToken}",
                        currentLine
                    )

                tokenStream.consume()
                continue
            }

            break
        }

        if (tokenStream.currentToken.type != TokenType.RBRACKET)
            throw SyntaxError(
                "Expected ']' after array literal value list, or ',' and more values, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return ArrayLiteralNode(line, values)
    }

    private fun parseReturnStatement(): ReturnStatementNode {
        if (tokenStream.currentToken.type != TokenType.KW_RETURN)
            throw SyntaxError(
                "Expected return statement, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        val expr = parseExpression()

        return ReturnStatementNode(line, expr)
    }

    private fun parseContinueStatement(): ContinueStatementNode {
        if (tokenStream.currentToken.type != TokenType.KW_CONTINUE)
            throw SyntaxError(
                "Expected continue statement, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        return ContinueStatementNode(line)
    }

    private fun parseBreakStatement(): BreakStatementNode {
        if (tokenStream.currentToken.type != TokenType.KW_BREAK)
            throw SyntaxError(
                "Expected break statement, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        return BreakStatementNode(line)
    }

    private fun parseExpression(): ExpressionNode {
        val line = currentLine

        var left: ExpressionNode = parseTerm()
        var op: BinaryOperationType?
        while (BinaryOperationType.additiveFromTokenType(tokenStream.currentToken.type).also { op = it } != null) {
            tokenStream.consume()

            val expr = parseTerm()
            left = BinaryOperationNode(line, left, expr, op!!)
        }

        return left
    }

    private fun parseTerm(): ExpressionNode {
        val line = currentLine

        var left = parseFactor()

        var op: BinaryOperationType?
        while (BinaryOperationType.multiplicativeFromTokenType(tokenStream.currentToken.type)
                .also { op = it } != null
        ) {
            tokenStream.consume()

            val expr = parseFactor()
            left = BinaryOperationNode(line, left, expr, op!!)
        }
        return left
    }

    private fun parseSubExpression(): ExpressionNode {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxError(
                "Expected '(' at the start of a sub-expression, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val expr = parseExpression()

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxError(
                "Expected ')' at the end of a sub-expression, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return expr
    }

    private fun parseFactor(): ExpressionNode {
        var factor = parsePrimaryFactor()

        // Handle member accesses, calls, and index accesses
        while (true) {
            factor = when (tokenStream.currentToken.type) {
                TokenType.DOT      -> {
                    tokenStream.consume()
                    if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
                        throw SyntaxError("Expected member identifier", currentLine)
                    val member = tokenStream.currentToken
                    tokenStream.consume()
                    MemberAccessNode(factor.line, factor, member)
                }

                TokenType.LPAREN   -> {
                    val params = parseCallParameters()
                    ExpressionCallNode(factor.line, factor, params)
                }

                TokenType.LBRACKET -> parseIndexAccess(factor)

                else               -> break
            }
        }

        if (tokenStream.currentToken.type == TokenType.EQUALS)
            return parseVariableAssignment(factor)

        return factor
    }

    private fun parsePrimaryFactor(): ExpressionNode {
        val line = currentLine

        // Literals
        var factor: ExpressionNode? = when (tokenStream.currentToken.type) {
            TokenType.FLOAT_LITERAL   -> LiteralNode.FloatLiteral(line, tokenStream.currentToken.value.toDouble())
            TokenType.INTEGER_LITERAL -> LiteralNode.IntegerLiteral(line, tokenStream.currentToken.value.toInt())
            TokenType.STRING_LITERAL  -> LiteralNode.StringLiteral(line, tokenStream.currentToken.value)
            TokenType.KW_TRUE         -> LiteralNode.BooleanLiteral(line, true)
            TokenType.KW_FALSE        -> LiteralNode.BooleanLiteral(line, false)
            else                      -> null
        }

        if (factor != null) {
            tokenStream.consume()
            return factor
        }

        // Complex Expressions
        factor = when (tokenStream.currentToken.type) {
            TokenType.KW_VAR      -> parseVariableDeclaration()
            TokenType.KW_IF       -> parseConditionalExpression()
            TokenType.KW_LOOP     -> parseLoopExpression()
            TokenType.KW_FUN      -> parseFunctionDefinition()
            TokenType.LPAREN      -> parseSubExpression()
            TokenType.KW_INTERNAL -> parseInternalFunctionCallExpression()
            TokenType.KW_NEW      -> parseComplexTypeInstantiation()
            TokenType.LBRACKET    -> parseArrayLiteral()
            TokenType.BAR         -> parseLengthExpression()
            else                  -> null
        }

        if (factor != null) {
            return factor
        }

        if (tokenStream.currentToken.type == TokenType.IDENTIFIER) {
            factor = parseVariableReference()
        }

        return factor ?: throw SyntaxError(
            "Expected an expression factor, got ${tokenStream.currentToken}",
            currentLine
        )
    }

    private fun parseIndexAccess(target: ExpressionNode): IndexAccessNode {
        if (tokenStream.currentToken.type != TokenType.LBRACKET)
            throw SyntaxError(
                "Expected '[' at the start of an index access, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        val index = parseExpression()

        if (tokenStream.currentToken.type != TokenType.RBRACKET)
            throw SyntaxError(
                "Expected ']' after index access expression, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return IndexAccessNode(line, target, index)
    }

    private fun parseLengthExpression(): LengthExpressionNode {
        if (tokenStream.currentToken.type != TokenType.BAR)
            throw SyntaxError(
                "Expected '|' at the start of a length expression, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        val expr = parseExpression()

        if (tokenStream.currentToken.type != TokenType.BAR)
            throw SyntaxError(
                "Expected '|' after length expression, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return LengthExpressionNode(line, expr)
    }

    private fun parseVariableDeclaration(): VariableDeclarationNode {
        if (tokenStream.currentToken.type != TokenType.KW_VAR)
            throw SyntaxError(
                "Expected 'var' keyword at the beginning of a variable declaration, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected variable identifier, got ${tokenStream.currentToken}",
                currentLine
            )

        val id = tokenStream.currentToken

        tokenStream.consume()

        var type: DataType = DataType.InferredType

        if (tokenStream.currentToken.type == TokenType.COLON) {
            tokenStream.consume()
            type = parseDataType()
        }

        if (tokenStream.currentToken.type != TokenType.EQUALS) {
            return VariableDeclarationNode(line, id, type, null)
        }

        tokenStream.consume()

        return VariableDeclarationNode(line, id, type, parseExpression())
    }

    private fun parseVariableAssignment(target: ExpressionNode): AssignmentNode {
        val line = target.line

        if (tokenStream.currentToken.type != TokenType.EQUALS)
            throw SyntaxError(
                "Expected '=' after symbol path, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val expr = parseExpression()

        return AssignmentNode(line, target, expr)
    }

    private fun parseVariableReference(): SymbolReferenceNode {
        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected variable identifier, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        val id = tokenStream.currentToken

        tokenStream.consume()

        return SymbolReferenceNode(line, id)
    }

    private fun parseBlockExpression(): BlockExpressionNode {
        val line = currentLine

        if (tokenStream.currentToken.type != TokenType.LBRACE)
            throw SyntaxError(
                "Expected '{' at the start of a block expression, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val nodes = mutableListOf<Node>()
        val parent = if (blockHierarchy.isEmpty()) null else blockHierarchy.peek()
        val block = BlockExpressionNode(line, nodes, parent)
        blockHierarchy.push(block)

        while (tokenStream.currentToken.type != TokenType.RBRACE && !tokenStream.currentToken.isEof()) {
            nodes.add(
                parseAny()
            )
        }

        blockHierarchy.pop()

        if (tokenStream.currentToken.type != TokenType.RBRACE)
            throw SyntaxError(
                "Reached end of file while parsing block expression starting on line $line",
                currentLine
            )

        tokenStream.consume()

        return BlockExpressionNode(line, nodes, parent)
    }

    private fun parseConditionalExpressionBranch(): ConditionalExpressionNode.ConditionalExpressionCollection? {
        if (tokenStream.currentToken.type != TokenType.KW_IF)
            throw SyntaxError(
                "Expected 'if' keyword at the start of a conditional expression, got ${tokenStream.currentToken}",
                currentLine
            )

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
                throw SyntaxError(
                    "A conditional expression cannot have more than one default branch",
                    currentLine
                )

            tokenStream.consume()

            val block = parseBlockExpression()
            collectBranch.defaultBranch = block
        }

        return collectBranch
    }

    private fun parseConditionalExpression(): ConditionalExpressionNode {
        val line = currentLine

        val collection = parseConditionalExpressionBranch() ?: throw SyntaxError(
            "Conditional expression tree does not exist in conditional expression.",
            currentLine
        )

        return ConditionalExpressionNode(line, collection.branches, collection.defaultBranch)
    }

    private fun parseFunctionSignature(named: Boolean): List<FunctionDefinitionNode.FunctionSignatureParameter> {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxError(
                "Expected '(' at the start of a function signature, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val parameters = mutableListOf<FunctionDefinitionNode.FunctionSignatureParameter>()

        while (tokenStream.currentToken.type != TokenType.RPAREN && !tokenStream.currentToken.isEof()) {
            var paramIdentifier: Token = Token("", TokenType.IDENTIFIER, 0)

            if (named) {
                if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
                    throw SyntaxError(
                        "Expected parameter identifier, got ${tokenStream.currentToken}",
                        currentLine
                    )

                paramIdentifier = tokenStream.currentToken

                tokenStream.consume()

                if (tokenStream.currentToken.type != TokenType.COLON)
                    throw SyntaxError(
                        "Expected ':' after function signature parameter, got ${tokenStream.currentToken}",
                        currentLine
                    )

                tokenStream.consume()
            }

            val paramType = parseDataType()

            parameters.add(FunctionDefinitionNode.FunctionSignatureParameter(paramIdentifier, paramType))

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RPAREN)
                    throw SyntaxError(
                        "Expected function signature parameter after ',', got ${tokenStream.nextToken}",
                        currentLine
                    )

                tokenStream.consume()
                continue
            }

            break
        }

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxError(
                "Expected ')' at the end of a function signature, or ',' and more parameters, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return parameters
    }

    private fun parseFunctionDefinition(): FunctionDefinitionNode {
        if (tokenStream.currentToken.type != TokenType.KW_FUN)
            throw SyntaxError(
                "Expected 'fun' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

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

        return FunctionDefinitionNode(line, identifier, anon, parameters, returnType, block)
    }

    private fun parseCallParameters(): List<ExpressionCallNode.CallParameter> {
        if (tokenStream.currentToken.type != TokenType.LPAREN)
            throw SyntaxError(
                "Expected '(' after function identifier, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val parameters = mutableListOf<ExpressionCallNode.CallParameter>()

        while (tokenStream.currentToken.type != TokenType.RPAREN && !tokenStream.currentToken.isEof()) {
            val expr = parseExpression()

            parameters.add(ExpressionCallNode.CallParameter(expr))

            if (tokenStream.currentToken.type == TokenType.COMMA) {
                if (tokenStream.nextToken.type == TokenType.RBRACE)
                    throw SyntaxError(
                        "Expected call parameter after ',', got ${tokenStream.nextToken}",
                        currentLine
                    )

                tokenStream.consume()
                continue
            }

            break
        }

        if (tokenStream.currentToken.type != TokenType.RPAREN)
            throw SyntaxError(
                "Expected ')' after function call parameter list or ',' and more parameters, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        return parameters
    }

    private fun parseInternalFunctionCallExpression(): InternalFunctionCallNode {
        if (tokenStream.currentToken.type != TokenType.KW_INTERNAL)
            throw SyntaxError(
                "Expected 'internal' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected internal function identifier, got ${tokenStream.currentToken.type}",
                currentLine
            )

        val identifier = tokenStream.currentToken

        tokenStream.consume()

        val params = parseCallParameters()

        return InternalFunctionCallNode(line, identifier, params)
    }

    private fun parseLoopBound(): LoopExpressionNode.LoopBound {
        val exclusive: Boolean

        when (tokenStream.currentToken.type) {
            TokenType.KW_EXCL -> {
                exclusive = true
                tokenStream.consume()
            }

            TokenType.KW_INCL -> {
                exclusive = false
                tokenStream.consume()
            }

            else              -> {
                exclusive = false
            }
        }

        val expr = parseExpression()

        return LoopExpressionNode.LoopBound(expr, exclusive)
    }

    private fun parseLoopExpression(): LoopExpressionNode {
        if (tokenStream.currentToken.type != TokenType.KW_LOOP)
            throw SyntaxError(
                "Expected 'loop' keyword, got ${tokenStream.currentToken}",
                currentLine
            )

        val line = currentLine

        tokenStream.consume()

        if (tokenStream.currentToken.type != TokenType.IDENTIFIER)
            throw SyntaxError(
                "Expected index variable identifier, got ${tokenStream.currentToken}",
                currentLine
            )

        val loopVarIdentifier = tokenStream.currentToken

        tokenStream.consume()

        val from = parseLoopBound()

        if (tokenStream.currentToken.type != TokenType.RIGHT_ARROW)
            throw SyntaxError(
                "Expected '->' after lower loop bound, got ${tokenStream.currentToken}",
                currentLine
            )

        tokenStream.consume()

        val to = parseLoopBound()

        val step: ExpressionNode

        if (tokenStream.currentToken.type == TokenType.KW_STEP) {
            tokenStream.consume()
            step = parseExpression()
        } else {
            step = LiteralNode.IntegerLiteral(line, 1)
        }

        val block = parseBlockExpression()

        return LoopExpressionNode(line, from, to, step, block, loopVarIdentifier)
    }

}
