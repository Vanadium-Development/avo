package org.vanadium.avo.runtime

import org.vanadium.avo.types.DataType

data class Variable(
    val scope: Scope,
    var value: RuntimeValue,
    val type: DataType
)
