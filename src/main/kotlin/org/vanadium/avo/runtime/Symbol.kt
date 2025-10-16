package org.vanadium.avo.runtime

import org.vanadium.avo.syntax.ast.BlockExpressionNode
import org.vanadium.avo.syntax.ast.FunctionDefinitionNode
import org.vanadium.avo.types.DataType

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