package dev.vanadium.avo

import dev.vanadium.avo.error.handler.MordantErrorHandler
import dev.vanadium.avo.runtime.interpreter.internal.InternalConsoleFunctions

fun main() {
    Interpreter {
        sourcePath { "grammar.avo" }
        functionLoaderSource(InternalConsoleFunctions::class)
        errorHandling {
            exitOnError()
            handlerImplementation { MordantErrorHandler }
        }
    }.exists {
        run()
    }
}