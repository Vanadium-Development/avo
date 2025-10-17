package dev.vanadium.avo.runtime.interpreter

import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.expression.*
import dev.vanadium.avo.syntax.ast.*
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.isSuperclassOf

class Interpreter {
    var scopes: Stack<Scope> = Stack()
    val scope get() = scopes.peek()

    private val interpreters: HashMap<KClass<out ExpressionNode>, ExpressionInterpreter<out ExpressionNode>> =
        hashMapOf()

    init {
        scopes.push(Scope(null))
        registerInterpreter<BinaryOperationNode>(BinaryOperationInterpreter::class)
        registerInterpreter<ExpressionCallNode>(ExpressionCallInterpreter::class)
        registerInterpreter<FunctionDefinitionNode>(FunctionDefinitionInterpreter::class)
        registerInterpreter<LiteralNode>(LiteralExpressionInterpreter::class)
        registerInterpreter<VariableAssignmentNode>(VariableAssignmentInterpreter::class)
        registerInterpreter<VariableDeclarationNode>(VariableDeclarationInterpreter::class)
        registerInterpreter<SymbolReferenceNode>(SymbolReferenceInterpreter::class)
        registerInterpreter<BlockExpressionNode>(BlockExpressionInterpreter::class)
        registerInterpreter<ConditionalExpressionNode>(ConditionalExpressionInterpreter::class)
    }

    private inline fun <reified T : ExpressionNode> registerInterpreter(clazz: KClass<out ExpressionInterpreter<T>>) {
        val constructor = clazz.constructors.firstOrNull {
            it.parameters.size == 1 && Interpreter::class.isSuperclassOf(
                it.parameters[0].type.classifier as KClass<*>
            )
        } ?: throw RuntimeException(
            "Could not register interpreter. No fitting constructor found for \"${clazz.simpleName}\""
        )
        interpreters[T::class] = constructor.call(this)
    }

    @Suppress("UNCHECKED_CAST")
    private fun evaluateAny(node: ExpressionNode): RuntimeValue {
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

    fun evaluate(node: ExpressionNode): RuntimeValue = evaluateAny(node)
}