package dev.vanadium.avo.module

import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode
import dev.vanadium.avo.syntax.ast.ModuleNode
import java.io.File

data class Module(
    val path: File,
    val module: ModuleNode
) {
    fun findMainFunctionDefinition(): FunctionDefinitionNode {
        val definitions = module.nodes.filter { it is FunctionDefinitionNode && it.identifier.value == "main" }
        if (definitions.isEmpty())
            throw SourceError(
                "Module \"${module.name}\" does not contain a main function",
                path.name
            )
        if (definitions.size > 1)
            throw SourceError(
                "Module \"${module.name}\" contains more than one main function",
                path.name
            )

        val definition = definitions[0] as FunctionDefinitionNode

        if (definition.returnType != DataType.VoidType)
            throw SourceError(
                "Return type of main function in module \"${module.name}\" is not void",
                path.name
            )

        if (definition.parameters.isNotEmpty())
            throw SourceError(
                "Main function in module \"${module.name}\" must have no parameters",
                path.name
            )

        return definition
    }
}