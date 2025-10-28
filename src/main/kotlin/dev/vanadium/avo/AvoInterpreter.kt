package dev.vanadium.avo

import dev.vanadium.avo.error.BaseError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.handler.ErrorHandlingConfig
import dev.vanadium.avo.module.ModuleLoader
import dev.vanadium.avo.module.SourceScanner
import dev.vanadium.avo.runtime.internal.InternalFunctionLoader
import dev.vanadium.avo.runtime.interpreter.Runtime
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
        val entryCall = mainFunction.noParameterCall() ?: throw SourceError(
            "Could not invoke main function in module \"${main.module.name}\"",
            main.path.name
        )

        main.module.nodes.add(entryCall)

        runtime.runModule(main.module)
    }
}