package org.vanadium.avo.runtime.interpreter.expression

import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.FunctionDefinitionNode

class FunctionDefinitionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<FunctionDefinitionNode>(
    interpreter
) {

    override fun evaluate(node: FunctionDefinitionNode): RuntimeValue {
        val function = scope.defineFunction(
            node.identifier.value,
            node.parameters,
            node.returnType,
            node.block
        )

        return RuntimeValue.LambdaValue(function)
    }
}