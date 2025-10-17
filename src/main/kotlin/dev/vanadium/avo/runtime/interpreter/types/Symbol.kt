package dev.vanadium.avo.runtime.interpreter.types

import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.syntax.ast.BlockExpressionNode
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode
import dev.vanadium.avo.types.DataType

sealed class Symbol {
    data class Function(
        val scope: Scope,
        val identifier: String?,
        val signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
        val returnType: DataType,
        val block: BlockExpressionNode
    ) : Symbol() {
        fun name(): String {
            return identifier ?: "<anonymous function>"
        }
    }

    data class Variable(
        val scope: Scope,
        var value: RuntimeValue,
        val type: DataType
    ) : Symbol()
}