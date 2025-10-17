package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.ConditionalExpressionNode

class ConditionalExpressionInterpreter(interpreter: Interpreter) :
    ExpressionInterpreter<ConditionalExpressionNode>(interpreter) {
    override fun evaluate(node: ConditionalExpressionNode): ControlFlowResult {
        node.branches.firstOrNull f@{
            val conditionResult = evaluateOther(it.condition)

            if (conditionResult !is ControlFlowResult.Value)
                throw AvoRuntimeException(
                    "Conditional expression cannot evaluate to a ${conditionResult.name()}"
                )

            val condition = conditionResult.runtimeValue

            if (condition !is RuntimeValue.BooleanValue)
                throw AvoRuntimeException("Conditional expression must be a boolean value.")

            if (!condition.value)
                return@f false

            return evaluateOther(it.block)
        }

        if (node.defaultBranch == null)
            return ControlFlowResult.Value(RuntimeValue.VoidValue())

        return evaluateOther(node.defaultBranch)
    }
}