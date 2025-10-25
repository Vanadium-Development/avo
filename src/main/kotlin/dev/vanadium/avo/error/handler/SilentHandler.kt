package dev.vanadium.avo.error.handler

import dev.vanadium.avo.error.LexerError
import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.SyntaxError

object SilentHandler : DefaultErrorHandler(), ErrorHandler {
    override fun syntaxError(error: SyntaxError) = Unit
    override fun runtimeError(error: RuntimeError) = Unit
    override fun sourceError(error: SourceError) = Unit
    override fun lexerError(error: LexerError) = Unit
}