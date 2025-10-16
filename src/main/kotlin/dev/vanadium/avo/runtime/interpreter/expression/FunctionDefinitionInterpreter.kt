package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

class FunctionDefinitionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<FunctionDefinitionNode>(
    interpreter
) {
    override fun evaluate(node: FunctionDefinitionNode): RuntimeValue {
        val function = scope.defineFunction(
            if (node.anonymous) null else node.identifier.value,
            node.parameters,
            node.returnType,
            node.block
        )

        return RuntimeValue.LambdaValue(function)
    }
}