package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.runtime.interpreter.types.DataType
import dev.vanadium.avo.syntax.lexer.Token

class KindDefinitionExpressionNode(
    @Transient
    override val line: Int,
    val identifier: Token,
    val fields: List<KindDefinitionField>
) : ExpressionNode(line) {
    data class KindDefinitionField(
        val identifier: Token,
        val dataType: DataType
    )
}
