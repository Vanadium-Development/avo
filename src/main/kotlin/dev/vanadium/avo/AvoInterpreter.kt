package dev.vanadium.avo

import dev.vanadium.avo.error.BaseError
import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.handler.ErrorHandlingConfig
import dev.vanadium.avo.module.ModuleLoader
import dev.vanadium.avo.module.SourceScanner
import dev.vanadium.avo.runtime.internal.InternalFunctionLoader
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.symbol.Function
import dev.vanadium.avo.syntax.ast.ExpressionCallNode
import dev.vanadium.avo.syntax.ast.SymbolReferenceNode
import java.io.File

class AvoInterpreter(
    val functionLoader: InternalFunctionLoader,
    val errorHandlingConfig: ErrorHandlingConfig
) {
    private val runtime = Runtime(functionLoader)

    private val loader = ModuleLoader(
        SourceScanner(
            File(
                System.getProperty("user.dir")
            )
        )
    )

    fun runMainModule() {
        try {
            run()
        } catch (e: BaseError) {
            errorHandlingConfig.handler.dispatch(e, errorHandlingConfig)
        }
    }

    private fun run() {
        loader.loadAllModules()
        val main = loader.findMainModule()
        val mainFunction = main.findMainFunctionDefinition()

        // Run the main module to generate function definitions (among other things)
        runtime.runModule(main.module)

        // Call the main function
        runtime.evaluate(ExpressionCallNode(
            mainFunction.line,
            SymbolReferenceNode(
                mainFunction.line,
                mainFunction.identifier
            ),
            listOf()
        ))
    }
}