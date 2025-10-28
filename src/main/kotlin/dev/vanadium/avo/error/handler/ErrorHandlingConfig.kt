package dev.vanadium.avo.error.handler

data class ErrorHandlingConfig(
    val handler: ErrorHandler,
    val exitOnError: Boolean
)