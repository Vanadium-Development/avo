package dev.vanadium.avo.syntax.lexer

import com.github.ajalt.mordant.input.InputEvent
import dev.vanadium.avo.error.LexerError

fun Char.isIdentifierChar(): Boolean {
    return isLetter() || this == '_'
}

class Lexer(var input: String) {
    private var position = 0
    private var line = 1

    val currentPosition get() = position
    val currentLine get() = line

    fun reset(newInput: String) {
        position = 0
        line = 1
        input = newInput
    }

    fun resetPosition(position: Int) {
        this.position = position
    }

    private fun peek(): Char? {
        return input.getOrNull(position)
    }

    private fun isEof(): Boolean = position >= input.length

    private fun advance() {
        position++
    }

    private fun retract() {
        if (position > 0)
            position--
    }

    private fun skipSpaces() {
        while (!isEof()) {
            val c = peek()!!
            if (!c.isWhitespace())
                break
            if (c == '\n') {
                line++
            }
            advance()
        }
    }

    fun hasNext(): Boolean {
        skipSpaces()
        return !isEof()
    }

    private fun Char.classify(currentType: TokenType): TokenType = when {
        isWhitespace()     -> TokenType.UNDEFINED
        isDigit()          -> if (currentType == TokenType.FLOAT_LITERAL) TokenType.FLOAT_LITERAL else TokenType.INTEGER_LITERAL
        isIdentifierChar() -> TokenType.IDENTIFIER
        else               -> TokenType.GENERIC_SYMBOL
    }

    fun nextToken(): Token {
        skipSpaces()
        if (isEof()) {
            return Token.eof()
        }

        var type = TokenType.UNDEFINED
        val token = StringBuilder()
        var isComment = false
        var isString = false

        while (!isEof()) {
            val c = peek()!!

            if (isComment) {
                if (c == '\n') {
                    skipSpaces()
                    isComment = false
                    continue
                }
                advance()
                continue
            }

            if (c == '#') {
                isComment = true
                advance()
                continue
            }

            if (c == '"') {
                isString = !isString
                type = TokenType.STRING_LITERAL
                advance()
                if (!isString) {
                    break
                }
                continue
            }

            if (isString) {
                token.append(c)
                advance()
                continue
            }

            if (c.isWhitespace()) {
                skipSpaces()
                break
            }

            if (c == '.' && type == TokenType.INTEGER_LITERAL) {
                type = TokenType.FLOAT_LITERAL
                token.append(c)
                advance()
                continue
            }

            var nextType = c.classify(type)
            val isSymbol = nextType == TokenType.GENERIC_SYMBOL
            if (isSymbol) {
                nextType = c.toString().findTokenType() ?: throw LexerError("Unknown symbol: $c", line)
            }
            if (type == TokenType.UNDEFINED) type = nextType
            else if (nextType != type) break

            token.append(c)
            advance()

            if (isSymbol) {
                val first = c
                val second = peek() ?: break
                if (second.classify(nextType) != TokenType.GENERIC_SYMBOL) {
                    break
                }

                val compoundToken = "$first$second"
                val compoundType = compoundToken.findTokenType() ?: break

                type = compoundType
                token.append(second)
                advance()
                break
            }
        }

        return if (token.isEmpty() && type == TokenType.UNDEFINED) Token.eof()
        else Token(token.toString(), type, line).typeAdjusted()
    }

    fun tokenizeAll(): List<Token> {
        val tokens = mutableListOf<Token>()
        while (hasNext())
            tokens.add(nextToken())
        return tokens
    }

}