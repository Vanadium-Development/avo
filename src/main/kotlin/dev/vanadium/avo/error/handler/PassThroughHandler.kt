package dev.vanadium.avo.error.handler

import dev.vanadium.avo.error.LexerError
import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.SyntaxError

object PassThroughHandler : DefaultErrorHandler(), ErrorHandler {
    override fun syntaxError(error: SyntaxError) = throw error
    override fun runtimeError(error: RuntimeError) = throw error
    override fun sourceError(error: SourceError) = throw error
    override fun lexerError(error: LexerError) = throw error
}