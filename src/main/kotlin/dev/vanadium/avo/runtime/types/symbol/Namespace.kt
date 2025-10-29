package dev.vanadium.avo.runtime.types.symbol

import dev.vanadium.avo.runtime.Scope

class Namespace(
    val identifier: String,
    val scope: Scope
) : Symbol()