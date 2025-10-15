package org.vanadium.avo.runtime

import org.vanadium.avo.syntax.ast.BlockExpressionNode
import org.vanadium.avo.syntax.ast.FunctionDefinitionNode
import org.vanadium.avo.types.DataType

data class Function(
    val scope: Scope,
    val signature: List<FunctionDefinitionNode.FunctionSignatureParameter>,
    val returnType: DataType,
    val block: BlockExpressionNode
)