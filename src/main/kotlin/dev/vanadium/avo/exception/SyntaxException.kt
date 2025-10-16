package dev.vanadium.avo.exception

class SyntaxException(override val message: String) : RuntimeException("Syntax error: $message")