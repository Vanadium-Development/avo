package dev.vanadium.avo

import dev.vanadium.avo.error.BaseError
import dev.vanadium.avo.error.handler.ErrorHandlingConfig
import dev.vanadium.avo.module.ModuleLoader
import dev.vanadium.avo.module.SourceScanner
import dev.vanadium.avo.runtime.internal.InternalFunctionLoader
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.syntax.ast.ExpressionCallNode
import dev.vanadium.avo.syntax.ast.SymbolReferenceNode
import dev.vanadium.avo.syntax.lexer.Token
import dev.vanadium.avo.syntax.lexer.TokenType
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

        // Resolve Imports
        main.module.imports.forEach { import ->
            // TODO Circular Dependency Check, Etc.
            val module = loader.findModule(import.identifier.value, main)
            val moduleRuntime = Runtime(functionLoader)
            moduleRuntime.runModule(module.module)
            runtime.scope.defineNamespace(
                module.module.name,
                moduleRuntime.scope,
                import.line
            )
        }

        runtime.runModule(main.module)

        // Call the main function
        runtime.evaluate(
            ExpressionCallNode(
                0,
                SymbolReferenceNode(
                    0,
                    Token("main", TokenType.IDENTIFIER, 0)
                ),
                listOf()
            )
        )
    }
}