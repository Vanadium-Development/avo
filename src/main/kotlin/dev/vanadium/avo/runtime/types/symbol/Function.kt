package dev.vanadium.avo.runtime.types.symbol

import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.syntax.ast.BlockExpressionNode
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

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