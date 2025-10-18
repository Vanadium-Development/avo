package dev.vanadium.avo.error.handler

object ErrorHandlingConfig {
    var handler: ErrorHandler = MordantErrorHandler
    var exitOnError: Boolean = true

    fun exitOnError() {
        exitOnError = true
    }

    fun noExitOnError() {
        exitOnError = false
    }

    fun handlerImplementation(handler: () -> ErrorHandler) {
        this.handler = handler()
    }
}