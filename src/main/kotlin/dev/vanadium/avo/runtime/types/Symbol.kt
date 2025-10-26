package dev.vanadium.avo.runtime.types

import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.syntax.ast.BlockExpressionNode
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

sealed class Symbol {
    data class Function(
        val scope: Scope,
        val identifier: String?,
        val signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
        val returnType: DataType,
        val block: BlockExpressionNode
    ) : Symbol() {
        fun name(): String {
            return if (identifier == null) "<anonymous>" else "\"$identifier\""
        }
    }

    data class Variable(
        val scope: Scope,
        var value: RuntimeValue,
        val type: DataType
    ) : Symbol()
}