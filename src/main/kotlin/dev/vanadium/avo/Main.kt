package dev.vanadium.avo

import dev.vanadium.avo.error.handler.ErrorHandlingConfig
import dev.vanadium.avo.error.handler.MordantErrorHandler
import dev.vanadium.avo.runtime.internal.InternalConsoleFunctions
import dev.vanadium.avo.runtime.internal.InternalFunctionLoader
import dev.vanadium.avo.runtime.internal.InternalMathFunctions

fun main(args: Array<String>) {
    val functionLoader = InternalFunctionLoader()
    functionLoader.registerAll(
        InternalConsoleFunctions::class,
        InternalMathFunctions::class
    )
    AvoInterpreter(
        functionLoader,
        ErrorHandlingConfig(
            MordantErrorHandler,
            true
        )
    ).runMainModule()
}