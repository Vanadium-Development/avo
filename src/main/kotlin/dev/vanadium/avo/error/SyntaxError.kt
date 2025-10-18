package dev.vanadium.avo.error

class SyntaxError(
    override val message: String,
    val line: Int
) : BaseError()