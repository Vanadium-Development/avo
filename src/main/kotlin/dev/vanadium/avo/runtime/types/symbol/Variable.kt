package dev.vanadium.avo.runtime.types.symbol

import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.runtime.types.value.RuntimeValue

data class Variable(
    val identifier: String,
    val scope: Scope,
    var value: RuntimeValue,
    val type: DataType
) : Symbol()