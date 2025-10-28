package dev.vanadium.avo.runtime.internal

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.types.value.KotlinMappable
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.runtime.types.value.VoidValue
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.primaryConstructor

class InternalFunctionLoader {

    private val classList = mutableListOf<Any>()

    fun registerClass(clazz: KClass<*>) {
        val constr = clazz.primaryConstructor ?: throw RuntimeError(
            "$clazz does not have a primary constructor",
            0
        )
        classList.add(constr.call())
    }

    fun registerAll(vararg classes: KClass<*>) {
        classes.forEach { registerClass(it) }
    }

    fun invokeFunction(
        line: Int,
        identifier: String,
        parameters: List<RuntimeValue>
    ): RuntimeValue {
        if (classList.isEmpty())
            throw RuntimeError(
                "Cannot invoke function \"$identifier\": No source classes for internal function invocations were set",
                line
            )

        parameters.forEachIndexed { index, param ->
            if (param !is KotlinMappable)
                throw RuntimeError(
                    "Parameter ${index + 1} in call to internal function \"$identifier\" is not Kotlin-mappable.",
                    line
                )
        }

        var function: KFunction<*>? = null
        var instance: Any? = null

        for (inst in classList) {
            val clazz = inst::class
            val f = clazz.declaredFunctions.filter { fn ->
                !fn.hasAnnotation<AvoInternalExclude>() &&
                fn.name == identifier &&
                fn.parameters.drop(1)
                    .map { p -> p.type } == parameters.map { p ->
                    (p as KotlinMappable).toKotlinType()
                }
            }
            if (f.isEmpty())
                continue
            if (f.size > 1)
                throw RuntimeError(
                    "Internal function \"$identifier\" refers to multiple kotlin functions.",
                    line
                )
            function = f.first()
            instance = inst
            break
        }

        if (function == null)
            throw RuntimeError(
                "Could not find a definition for internal function \"$identifier\".",
                line
            )

        val returnValue = function.call(instance, *(parameters.map {
            (it as KotlinMappable).toKotlinValue()
        }.toTypedArray()))
        if (returnValue == null) {
            return VoidValue()
        }
        return RuntimeValue.fromKotlinValue(returnValue)
    }

}