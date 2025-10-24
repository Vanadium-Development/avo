package dev.vanadium.avo.runtime.interpreter.expression

/**
 * Marks a class as an expression interpreter to be
 * automatically discovered and loaded by the runtime.
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExpressionInterpreterImplementation