package dev.vanadium.avo.error.handler

import dev.vanadium.avo.error.BaseError
import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.SyntaxError
import kotlin.system.exitProcess

interface ErrorHandler {
    fun syntaxError(error: SyntaxError)
    fun runtimeError(error: RuntimeError)
    fun sourceError(error: SourceError)

    fun dispatch(e: BaseError) {
        when (e) {
            is SyntaxError  -> syntaxError(e)
            is RuntimeError -> runtimeError(e)
            is SourceError  -> sourceError(e)
            else            -> Unit
        }

        if (ErrorHandlingConfig.exitOnError)
            exitProcess(1)
    }
}

sealed class DefaultErrorHandler