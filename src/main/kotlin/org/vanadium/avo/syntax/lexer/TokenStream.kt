package org.vanadium.avo.syntax.lexer

class TokenStream(private val lexer: Lexer) {
    private var current: Token = Token.eof()
    private var next: Token = Token.eof()

    val currentToken get() = current
    val nextToken get() = next

    init {
        consume()
        consume()
    }

    fun consume() {
        current = next
        next = lexer.nextToken()
    }

}