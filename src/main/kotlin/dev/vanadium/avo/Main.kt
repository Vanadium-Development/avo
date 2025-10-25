package dev.vanadium.avo

import dev.vanadium.avo.error.handler.MordantErrorHandler
import dev.vanadium.avo.runtime.internal.InternalConsoleFunctions
import dev.vanadium.avo.runtime.internal.InternalMathFunctions
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Expected input file name.")
        return
    }

    Interpreter {
        errorHandling {
            handlerImplementation {
                MordantErrorHandler
            }
            exitOnError()
        }
        sourcePath {
            args.first()
        }
        functionLoaderSource(InternalMathFunctions::class)
        functionLoaderSource(InternalConsoleFunctions::class)
    }.exists {
        run()
    }.notExists {
        exitProcess(1)
    }
}