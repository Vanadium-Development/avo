package dev.vanadium.avo

import dev.vanadium.avo.error.handler.MordantErrorHandler
import dev.vanadium.avo.runtime.interpreter.internal.InternalConsoleFunctions
import dev.vanadium.avo.runtime.interpreter.internal.InternalMathFunctions

fun main() {
    Interpreter {
        sourcePath { "grammar.avo" }
        functionLoaderSource(InternalConsoleFunctions::class)
        functionLoaderSource(InternalMathFunctions::class)
        errorHandling {
            exitOnError()
            handlerImplementation { MordantErrorHandler }
        }
    }.exists {
        run()
    }
}