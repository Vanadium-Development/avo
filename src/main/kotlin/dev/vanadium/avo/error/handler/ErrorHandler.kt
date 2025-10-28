package dev.vanadium.avo.error.handler

import dev.vanadium.avo.error.*
import kotlin.system.exitProcess

interface ErrorHandler {
    fun syntaxError(error: SyntaxError)
    fun runtimeError(error: RuntimeError)
    fun sourceError(error: SourceError)
    fun lexerError(error: LexerError)

    fun dispatch(e: BaseError, config: ErrorHandlingConfig) {
        when (e) {
            is SyntaxError  -> syntaxError(e)
            is RuntimeError -> runtimeError(e)
            is SourceError  -> sourceError(e)
            is LexerError   -> lexerError(e)
            else            -> Unit
        }

        if (config.exitOnError)
            exitProcess(1)
    }
}

sealed class DefaultErrorHandler