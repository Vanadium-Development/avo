package dev.vanadium.avo.runtime.interpreter.internal

import kotlin.math.pow

class InternalMathFunctions {

    fun sqrt(d: Double): Double {
        return kotlin.math.sqrt(d)
    }

    fun pow(
        d: Double,
        exponent: Double
    ): Double {
        return d.pow(exponent)
    }

    fun sin(d: Double): Double {
        return kotlin.math.sin(d)
    }

    fun cos(d: Double): Double {
        return kotlin.math.cos(d)
    }

    fun tan(d: Double): Double {
        return kotlin.math.tan(d)
    }

    fun exp(d: Double): Double {
        return kotlin.math.exp(d)
    }

}