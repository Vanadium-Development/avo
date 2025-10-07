package org.vanadium.avo.syntax.lexer

import org.vanadium.avo.exception.LexerException

fun Char.isIdentifierChar(): Boolean {
    return isLetter() || this == '_'
}

class Lexer(val input: String) {

    private var position = 0

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
        while (!isEof() && peek()!!.isWhitespace()) {
            advance()
        }
    }

    fun hasNext(): Boolean {
        skipSpaces()
        return !isEof()
    }

    fun nextToken(): Token {
        skipSpaces()
        if (isEof()) {
            return Token.eof()
        }

        var type = TokenType.UNDEFINED
        val token = StringBuilder()

        while (position < input.length) {
            val c = peek()!!

            if (c.isWhitespace()) {
                skipSpaces()
                break
            }

            if (c == '.') {
                if (type == TokenType.INTEGER_LITERAL) {
                    type = TokenType.FLOAT_LITERAL
                    token.append(c)
                    advance()
                    continue
                } else if (type == TokenType.FLOAT_LITERAL) {
                    throw LexerException("Invalid float literal: \"${token}$c\"")
                }
            }

            if (c.isIdentifierChar()) {
                if (type != TokenType.UNDEFINED && type != TokenType.IDENTIFIER) {
                    return Token(token.toString(), type).typeAdjusted()
                }
                type = TokenType.IDENTIFIER
                token.append(c)
                advance()
                continue
            }

            if (c.isDigit()) {
                if (type != TokenType.UNDEFINED && type != TokenType.INTEGER_LITERAL && type != TokenType.FLOAT_LITERAL) {
                    return Token(token.toString(), type).typeAdjusted()
                }
                type = TokenType.INTEGER_LITERAL
                token.append(c)
                advance()
                continue
            }

            if (type != TokenType.UNDEFINED && token.isNotEmpty()) {
                return Token(token.toString(), type).typeAdjusted()
            }

            advance()

            token.append(c)
            return Token(token.toString(), type).typeAdjusted()
        }

        if (type != TokenType.UNDEFINED && token.isNotEmpty()) {
            return Token(token.toString(), type).typeAdjusted()
        }

        return Token.eof()
    }
}