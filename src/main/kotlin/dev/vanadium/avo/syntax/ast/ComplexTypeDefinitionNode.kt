package dev.vanadium.avo.syntax.ast

import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.syntax.lexer.Token

class ComplexTypeDefinitionNode(
    @Transient
    override val line: Int,
    val identifier: Token,
    val fields: List<ComplexTypeField>
) : StatementNode(line) {
    data class ComplexTypeField(
        val identifier: Token,
        val dataType: DataType
    )
}
