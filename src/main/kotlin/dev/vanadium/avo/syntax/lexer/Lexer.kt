package dev.vanadium.avo.syntax.lexer

import dev.vanadium.avo.exception.LexerException

fun Char.isIdentifierChar(): Boolean {
    return isLetter() || this == '_'
}

class Lexer(val input: String) {

    private var position = 0
    private var line = 1

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
        isWhitespace() -> TokenType.UNDEFINED
        isDigit() -> if (currentType == TokenType.FLOAT_LITERAL) TokenType.FLOAT_LITERAL else TokenType.INTEGER_LITERAL
        isIdentifierChar() -> TokenType.IDENTIFIER
        else -> TokenType.GENERIC_SYMBOL
    }

    fun nextToken(): Token {
        skipSpaces()
        if (isEof()) {
            return Token.eof()
        }

        var type = TokenType.UNDEFINED
        val token = StringBuilder()
        var isString = false

        while (!isEof()) {
            val c = peek()!!

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
                nextType = c.toString().findTokenType() ?: throw LexerException("Unknown symbol: $c")
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

        return if (token.isEmpty()) Token.eof()
        else Token(token.toString(), type, line).typeAdjusted()
    }
}