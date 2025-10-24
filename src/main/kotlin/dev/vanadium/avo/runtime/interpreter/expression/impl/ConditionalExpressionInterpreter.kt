package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.BooleanValue
import dev.vanadium.avo.runtime.types.value.VoidValue
import dev.vanadium.avo.syntax.ast.ConditionalExpressionNode

@ExpressionInterpreterImplementation
class ConditionalExpressionInterpreter(runtime: Runtime) :
    ExpressionInterpreter<ConditionalExpressionNode>(runtime) {
    override fun evaluate(node: ConditionalExpressionNode): ControlFlowResult {
        for (branch in node.branches) {
            val conditionResult = evaluateOther(branch.condition)

            if (conditionResult !is ControlFlowResult.Value)
                throw RuntimeError(
                    "Conditional expression cannot evaluate to a ${conditionResult.name()}",
                    node.line
                )

            val condition = conditionResult.runtimeValue

            if (condition !is BooleanValue)
                throw RuntimeError(
                    "Conditional expression must be a boolean value.",
                    node.line
                )

            if (!condition.value)
                continue

            return evaluateOther(branch.block)
        }

        if (node.defaultBranch == null)
            return ControlFlowResult.Value(VoidValue())

        return evaluateOther(node.defaultBranch)
    }
}