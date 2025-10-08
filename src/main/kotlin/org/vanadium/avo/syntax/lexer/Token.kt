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

    @KeywordToken("else")
    KW_ELSE,

    @KeywordToken("fun")
    KW_FUN,

    @KeywordToken("true")
    KW_TRUE,

    @KeywordToken("false")
    KW_FALSE,

    @KeywordToken("return")
    KW_RETURN,

    @KeywordToken("continue")
    KW_CONTINUE,

    @KeywordToken("break")
    KW_BREAK,

    @KeywordToken("int")
    KW_INT,

    @KeywordToken("float")
    KW_FLOAT,

    @KeywordToken("string")
    KW_STRING,

    @KeywordToken("bool")
    KW_BOOL,

    @KeywordToken("void")
    KW_VOID,

    @KeywordToken("returns")
    KW_RETURNS,

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
    DOT,

    @CharacterToken('?')
    QUESTION_MARK;

    fun isAdditiveOperation() = (this == PLUS || this == MINUS)

    fun isMultiplicativeOperation() = (this == ASTERISK || this == SLASH)

}

data class Token(val value: String, val type: TokenType, val line: Int) {
    companion object {
        fun eof(): Token = Token("", TokenType.EOF, 0)
    }

    /**
     * Creates a new token that may include a keyword or character type
     */
    fun typeAdjusted(): Token {
        return Token(this.value, this.value.findTokenType() ?: type, this.line)
    }

    override fun toString(): String {
        return "Token ( '$value' | $type | Line $line )"
    }

    fun isEof() = this.type == TokenType.EOF

}
