package dev.vanadium.avo.runtime.interpreter.statement

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.syntax.ast.ComplexTypeDefinitionNode
import dev.vanadium.avo.syntax.ast.StatementNode

class StatementInterpreter(val runtime: Runtime) {

    val scope get() = runtime.scope

    fun execute(node: StatementNode) = when (node) {
        is ComplexTypeDefinitionNode -> executeComplexTypeDefinition(node)
        else                         -> throw RuntimeError(
            "Could not interpret statement: ${node.javaClass.simpleName}",
            node.line
        )
    }

    private fun executeComplexTypeDefinition(node: ComplexTypeDefinitionNode) {
        scope.defineComplexType(node.identifier.value, node.fields, node.line)
    }

}