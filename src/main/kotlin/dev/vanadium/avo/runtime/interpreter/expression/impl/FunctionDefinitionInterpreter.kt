package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.LambdaValue
import dev.vanadium.avo.syntax.ast.FunctionDefinitionNode

@ExpressionInterpreterImplementation
class FunctionDefinitionInterpreter(runtime: Runtime) : ExpressionInterpreter<FunctionDefinitionNode>(
    runtime
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