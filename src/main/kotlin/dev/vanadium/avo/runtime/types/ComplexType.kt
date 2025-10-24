package dev.vanadium.avo.runtime.types

import dev.vanadium.avo.syntax.ast.ComplexTypeDefinitionNode

data class ComplexType(
    val identifier: String,
    val fields: List<ComplexTypeDefinitionNode.ComplexTypeField>,
    val definitionLine: Int
)