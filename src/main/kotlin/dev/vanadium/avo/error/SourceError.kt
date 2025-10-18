package dev.vanadium.avo.error

class SourceError(override val message: String, val file: String) : BaseError()