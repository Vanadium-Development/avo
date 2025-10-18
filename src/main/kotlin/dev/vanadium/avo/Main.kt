package dev.vanadium.avo

import dev.vanadium.avo.error.handler.MordantErrorHandler

fun main() {
    Interpreter {
        sourcePath { "grammar.avo" }
        errorHandling {
            exitOnError()
            handlerImplementation { MordantErrorHandler }
        }
    }.exists {
        run()
    }
}