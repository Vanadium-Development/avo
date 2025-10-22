package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.interpreter.InterpreterImpl
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.value.LambdaValue
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

@InterpreterImpl
class FunctionDefinitionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<FunctionDefinitionNode>(
    interpreter
) {
    override fun evaluate(node: FunctionDefinitionNode): ControlFlowResult {
        val function = scope.defineFunction(
            if (node.anonymous) null else node.identifier.value,
            node.parameters,
            node.returnType,
            node.block,
            node.line
        )

        return ControlFlowResult.Value(LambdaValue(function))
    }
}