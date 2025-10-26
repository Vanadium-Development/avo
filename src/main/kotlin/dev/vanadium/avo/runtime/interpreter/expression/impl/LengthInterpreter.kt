package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.ArrayValue
import dev.vanadium.avo.runtime.types.value.IntegerValue
import dev.vanadium.avo.runtime.types.value.StringValue
import dev.vanadium.avo.syntax.ast.LengthExpressionNode

@ExpressionInterpreterImplementation
class LengthInterpreter(runtime: Runtime) :
    ExpressionInterpreter<LengthExpressionNode>(runtime) {

    override fun evaluate(node: LengthExpressionNode): ControlFlowResult {
        val exprResult = evaluateOther(node.expression)

        if (exprResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Length expression cannot evaluate to a ${exprResult.name()}",
                node.line
            )

        val length = when (val exprValue = exprResult.runtimeValue) {
            is StringValue -> exprValue.value.length
            is ArrayValue  -> exprValue.value.size
            else           -> throw RuntimeError(
                "Cannot find length of expression of type ${exprValue.dataType()}",
                node.line
            )
        }

        return ControlFlowResult.Value(
            IntegerValue(
                length
            )
        )
    }
}