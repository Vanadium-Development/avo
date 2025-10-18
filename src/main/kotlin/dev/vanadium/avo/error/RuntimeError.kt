package dev.vanadium.avo.error

class RuntimeError(override val message: String, val line: Int) : BaseError()