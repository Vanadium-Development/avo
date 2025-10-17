package dev.vanadium.avo.runtime.interpreter.types

sealed class ControlFlowResult {
    abstract fun name(): String

    data class Value(val runtimeValue: RuntimeValue) : ControlFlowResult() {
        override fun name() = "Runtime Value"
    }

    class Break : ControlFlowResult() {
        override fun name() = "Break Statement"
    }

    class Continue : ControlFlowResult() {
        override fun name() = "Continue Statement"
    }
}