package dev.vanadium.avo.runtime.interpreter

import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.internal.InternalFunctionLoader
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.ExpressionNode
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class Interpreter(val functionLoader: InternalFunctionLoader) {
    var scopes: Stack<Scope> = Stack()
    val scope get() = scopes.peek()

    private val interpreters: HashMap<KClass<out ExpressionNode>, ExpressionInterpreter<out ExpressionNode>> =
        hashMapOf()

    init {
        scopes.push(Scope(null))
        scanInterpreters()
    }

    private fun scanInterpreters() {
        val reflections = Reflections("dev.vanadium.avo.runtime.interpreter.expression", Scanners.SubTypes)
        val classes = reflections.getSubTypesOf(ExpressionInterpreter::class.java)
            .filter { it.isAnnotationPresent(InterpreterImpl::class.java) }

        for (clazz in classes) {
            val kClazz = clazz.kotlin
            val typeArg = kClazz.supertypes
                .first { it.classifier == ExpressionInterpreter::class }
                .arguments.first().type!!.classifier // Get Generic Parameter of ExpressionInterpreter<T>

            val nodeType = if (typeArg is KClass<*> && ExpressionNode::class.java.isAssignableFrom(typeArg.java)) {
                @Suppress("UNCHECKED_CAST")
                typeArg as KClass<out ExpressionNode>
            } else throw RuntimeException("Could not register interpreter class: ${kClazz.simpleName}")

            registerInterpreter(nodeType, kClazz)
        }
    }

    private fun registerInterpreter(
        nodeType: KClass<out ExpressionNode>,
        clazz: KClass<out ExpressionInterpreter<*>>
    ) {
        val constructor = clazz.constructors.firstOrNull {
            it.parameters.size == 1 && Interpreter::class.isSuperclassOf(
                it.parameters[0].type.classifier as KClass<*>
            )
        } ?: throw RuntimeException(
            "Could not register interpreter. No fitting constructor found for \"${clazz.simpleName}\""
        )
        interpreters[nodeType] = constructor.call(this)
    }

    @Suppress("UNCHECKED_CAST")
    private fun evaluateAny(node: ExpressionNode): ControlFlowResult {
        var clazz: KClass<out ExpressionNode>? = node::class
        while (clazz != null) {
            val interpreter = interpreters[clazz]
            if (interpreter != null) {
                return (interpreter as ExpressionInterpreter<ExpressionNode>).evaluate(node)
            }
            clazz = clazz.supertypes.firstOrNull()?.classifier as? KClass<out ExpressionNode>
        }
        throw RuntimeException("Could not find interpreter for node \"${node.javaClass.simpleName}\"")
    }

    fun evaluate(node: ExpressionNode): ControlFlowResult = evaluateAny(node)
}