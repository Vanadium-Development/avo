package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

class FunctionDefinitionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<FunctionDefinitionNode>(
    interpreter
) {
    override fun evaluate(node: FunctionDefinitionNode): ControlFlowResult {
        val function = scope.defineFunction(
            if (node.anonymous) null else node.identifier.value,
            node.parameters,
            node.returnType,
            node.block
        )

        return ControlFlowResult.Value(RuntimeValue.LambdaValue(function))
    }
}