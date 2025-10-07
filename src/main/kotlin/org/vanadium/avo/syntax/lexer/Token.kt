package org.vanadium.avo.syntax.lexer

interface A

annotation class CharacterToken(val value: Char)

annotation class KeywordToken(val value: String)

/**
 * Find token type for keyword or character
 */
fun String.findTokenType(): TokenType? {
    val typeName = TokenType::class.java.declaredFields.filter {
        it.isAnnotationPresent(CharacterToken::class.java) ||
                it.isAnnotationPresent(KeywordToken::class.java)
    }.find {
        var annotation: Annotation? = it.getAnnotation(CharacterToken::class.java)
        if (annotation != null) {
            if (length != 1) {
                return null
            }
            val charAnnotation = annotation as CharacterToken
            if (charAnnotation.value == get(0)) {
                return@find true
            }
        }

        annotation = it.getAnnotation(KeywordToken::class.java)
        if (annotation != null) {
            var keywordAnnotation = annotation as KeywordToken
            if (keywordAnnotation.value == this) {
                return@find true
            }
        }

        return@find false
    }?.name

    if (typeName == null) {
        return null
    }

    return TokenType.valueOf(typeName)
}

enum class TokenType {
    EOF,
    UNDEFINED,

    GENERIC_SYMBOL,

    IDENTIFIER,

    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,

    @KeywordToken("var")
    KW_VAR,

    @KeywordToken("while")
    KW_WHILE,

    @KeywordToken("if")
    KW_IF,

    @KeywordToken("fn")
    KW_FN,

    @CharacterToken('(')
    LPAREN,

    @CharacterToken(')')
    RPAREN,

    @CharacterToken('{')
    LBRACE,

    @CharacterToken('}')
    RBRACE,

    @CharacterToken('[')
    LBRACKET,

    @CharacterToken(']')
    RBRACKET,

    @CharacterToken(',')
    COMMA,

    @CharacterToken(':')
    COLON,

    @CharacterToken(';')
    SEMICOLON,

    @CharacterToken('=')
    EQUALS,

    @CharacterToken('@')
    AT,

    @CharacterToken('&')
    AMPERSAND,

    @CharacterToken('+')
    PLUS,

    @CharacterToken('-')
    MINUS,

    @CharacterToken('/')
    SLASH,

    @CharacterToken('*')
    ASTERISK,

    @CharacterToken('<')
    LESS_THAN,

    @CharacterToken('>')
    GREATER_THAN,

    @CharacterToken('.')
    DOT
}

data class Token(val value: String, val type: TokenType) {
    companion object {
        fun eof(): Token = Token("", TokenType.EOF)
    }

    /**
     * Creates a new token that may include a keyword or character type
     */
    fun typeAdjusted(): Token {
        return Token(this.value, this.value.findTokenType() ?: type)
    }

    override fun toString(): String {
        return "Token ( '$value' | $type )"
    }


}
