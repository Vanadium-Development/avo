package dev.vanadium.avo.runtime.interpreter

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.internal.InternalFunctionLoader
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.statement.StatementInterpreter
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.ExpressionNode
import dev.vanadium.avo.syntax.ast.ModuleNode
import dev.vanadium.avo.syntax.ast.StatementNode
import org.reflections.Reflections
import org.reflections.scanners.Scanners
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class Runtime(val functionLoader: InternalFunctionLoader) {
    companion object {
        const val INTERPRETER_PKG = "dev.vanadium.avo.runtime.interpreter.expression.impl"
    }

    var scopes: Stack<Scope> = Stack()
    val scope get() = scopes.peek()

    private val interpreters: HashMap<KClass<out ExpressionNode>,
            ExpressionInterpreter<out ExpressionNode>> = hashMapOf()

    private val statementInterpreter = StatementInterpreter(this)

    init {
        scopes.push(Scope(null))
        scanInterpreters()
    }

    fun runModule(module: ModuleNode) {
        module.nodes.forEach { node ->
            when (node) {
                is ExpressionNode -> evaluate(node)
                is StatementNode  -> execute(node)
                else              -> throw RuntimeError(
                    "Unknown node type: ${node.javaClass.simpleName}",
                    node.line
                )
            }
        }
    }

    private fun scanInterpreters() {
        val reflections = Reflections(INTERPRETER_PKG, Scanners.SubTypes)
        val classes = reflections.getSubTypesOf(ExpressionInterpreter::class.java)
            .filter { it.isAnnotationPresent(ExpressionInterpreterImplementation::class.java) }

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
            it.parameters.size == 1 && Runtime::class.isSuperclassOf(
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

    /* Evaluate an Expression Node */
    fun evaluate(node: ExpressionNode): ControlFlowResult = evaluateAny(node)

    /* Execute a Statement Node */
    fun execute(node: StatementNode) = statementInterpreter.execute(node)
}