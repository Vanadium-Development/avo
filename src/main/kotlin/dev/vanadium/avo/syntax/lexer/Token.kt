package dev.vanadium.avo.syntax.lexer

annotation class Symbol(val value: Char)

annotation class CompoundSymbol(
    val first: Char,
    val second: Char
)

annotation class Keyword(val value: String)

/**
 * Find token type for keyword or character
 */
fun String.findTokenType(): TokenType? {
    val typeName = TokenType::class.java.declaredFields.filter {
        it.isAnnotationPresent(Symbol::class.java) || it.isAnnotationPresent(CompoundSymbol::class.java) || it.isAnnotationPresent(
            Keyword::class.java
        )
    }.find {
        var annotation: Annotation? = it.getAnnotation(Symbol::class.java)
        if (annotation != null) {
            if (length != 1) {
                return@find false
            }
            val charAnnotation = annotation as Symbol
            if (charAnnotation.value == get(0)) {
                return@find true
            }
        }

        annotation = it.getAnnotation(Keyword::class.java)
        if (annotation != null) {
            val keywordAnnotation = annotation
            if (keywordAnnotation.value == this) {
                return@find true
            }
        }

        annotation = it.getAnnotation(CompoundSymbol::class.java)
        if (annotation == null) return@find false

        if (this.length == 2) {
            val compoundSymbolAnnotation = annotation
            if (this[0] == compoundSymbolAnnotation.first && this[1] == compoundSymbolAnnotation.second) {
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
    EOF, UNDEFINED,

    GENERIC_SYMBOL,

    IDENTIFIER,

    INTEGER_LITERAL,
    FLOAT_LITERAL,
    STRING_LITERAL,

    //
    // Keywords
    //

    @Keyword("var") KW_VAR,

    @Keyword("while") KW_WHILE,

    @Keyword("if") KW_IF,

    @Keyword("else") KW_ELSE,

    @Keyword("fun") KW_FUN,

    @Keyword("true") KW_TRUE,

    @Keyword("false") KW_FALSE,

    @Keyword("return") KW_RETURN,

    @Keyword("continue") KW_CONTINUE,

    @Keyword("break") KW_BREAK,

    @Keyword("loop") KW_LOOP,

    @Keyword("excl") KW_EXCL,

    @Keyword("incl") KW_INCL,

    @Keyword("step") KW_STEP,

    @Keyword("int") KW_INT,

    @Keyword("float") KW_FLOAT,

    @Keyword("string") KW_STRING,

    @Keyword("bool") KW_BOOL,

    @Keyword("void") KW_VOID,

    @Keyword("complex") KW_COMPLEX,

    @Keyword("new") KW_NEW,

    @Keyword("internal") KW_INTERNAL,

    //
    // Symbols
    //

    @Symbol('(') LPAREN,

    @Symbol(')') RPAREN,

    @Symbol('{') LBRACE,

    @Symbol('}') RBRACE,

    @Symbol('[') LBRACKET,

    @Symbol(']') RBRACKET,

    @Symbol(',') COMMA,

    @Symbol(':') COLON,

    @Symbol(';') SEMICOLON,

    @Symbol('=') EQUALS,

    @Symbol('@') AT,

    @Symbol('&') AMPERSAND,

    @Symbol('+') PLUS,

    @Symbol('-') MINUS,

    @Symbol('/') SLASH,

    @Symbol('*') ASTERISK,

    @Symbol('<') LESS_THAN,

    @Symbol('>') GREATER_THAN,

    @Symbol('.') DOT,

    @Symbol('^') CARET,

    @Symbol('?') QUESTION_MARK,

    @Symbol('%') PERCENT,

    @Symbol('|') BAR,

    //
    // Compound Symbols
    //

    @CompoundSymbol('-', '>') RIGHT_ARROW,

    @CompoundSymbol('=', '=') DOUBLE_EQUALS,

    @CompoundSymbol('!', '=') NOT_EQUALS,

    @CompoundSymbol('>', '=') GREATER_EQUAL,

    @CompoundSymbol('<', '=') LESS_EQUAL;

    fun isAdditiveOperation() =
        (arrayOf(PLUS, MINUS, GREATER_THAN, LESS_THAN, GREATER_EQUAL, LESS_EQUAL, DOUBLE_EQUALS, NOT_EQUALS).contains(
            this
        ))

    fun isMultiplicativeOperation() = (this == ASTERISK || this == SLASH || this == CARET || this == PERCENT)

}

data class Token(
    val value: String,
    val type: TokenType,
    val line: Int
) {
    companion object {
        fun eof(): Token = Token("", TokenType.EOF, 0)
    }

    /**
     * Creates a new token that may include a keyword or character type
     */
    fun typeAdjusted(): Token {
        return Token(
            this.value,
            if (this.type == TokenType.GENERIC_SYMBOL || this.type == TokenType.IDENTIFIER)
                (this.value.findTokenType() ?: type) else type,
            this.line
        )
    }

    override fun toString(): String {
        if (type == TokenType.EOF)
            return "EOF"

        return "'$value'"
    }

    fun isEof() = this.type == TokenType.EOF

    fun asIdentifier(): String = if (type == TokenType.EOF) "<anonymous>" else "\"$value\""

}
