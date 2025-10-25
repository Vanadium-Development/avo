package dev.vanadium.avo.error

class LexerError(
    override val message: String,
    val line: Int
) : BaseError()