package dev.vanadium.avo.runtime.internal

class InternalConsoleFunctions {

    fun println(str: String) {
        kotlin.io.println(str)
    }

    fun print(str: String) {
        kotlin.io.print(str)
    }

}