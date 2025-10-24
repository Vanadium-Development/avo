package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.runtime.types.value.IntegerValue
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.runtime.types.value.VoidValue
import dev.vanadium.avo.syntax.ast.LoopExpressionNode

@ExpressionInterpreterImplementation
class LoopExpressionInterpreter(runtime: Runtime) : ExpressionInterpreter<LoopExpressionNode>(runtime) {
    override fun evaluate(node: LoopExpressionNode): ControlFlowResult {
        val lowerBoundResult = evaluateOther(node.start.expression)
        val upperBoundResult = evaluateOther(node.end.expression)
        val stepSizeResult = evaluateOther(node.step)

        if (lowerBoundResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Lower bound of loop cannot evaluate to ${lowerBoundResult.name()}",
                node.line
            )

        if (upperBoundResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Upper bound of loop cannot evaluate to ${upperBoundResult.name()}",
                node.line
            )

        if (stepSizeResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Step size of loop cannot evaluate to ${stepSizeResult.name()}",
                node.line
            )

        val lowerBound = lowerBoundResult.runtimeValue

        if (lowerBound.dataType() != DataType.IntegerType)
            throw RuntimeError(
                "Lower bound of loop is an integer: ${lowerBound.dataType()}",
                node.line
            )

        val upperBound = upperBoundResult.runtimeValue

        if (upperBound.dataType() != DataType.IntegerType)
            throw RuntimeError(
                "Upper bound of loop is not an integer: ${upperBound.dataType()}",
                node.line
            )

        val stepSize = stepSizeResult.runtimeValue

        if (stepSize.dataType() != DataType.IntegerType)
            throw RuntimeError(
                "Step size of loop is not an integer: ${stepSize.dataType()}",
                node.line
            )

        val start = (lowerBound as IntegerValue).value
        val end = (upperBound as IntegerValue).value
        val stepInt = (stepSize as IntegerValue).value

        if (start > end)
            throw RuntimeError(
                "Upper loop bound must be greater than the lower bound: $start -> $end",
                node.line
            )

        if (stepInt <= 0)
            throw RuntimeError(
                "Step size of loop must be > 0, got $stepInt",
                node.line
            )

        val actualStart = if (node.start.exclusive) start + 1 else start
        val actualEnd = if (node.end.exclusive) end - 1 else end
        val loop = actualStart..actualEnd step stepInt

        var returnValue: RuntimeValue = VoidValue()

        for (i in loop) {
            val loopScope = Scope(scope)
            pushScope(loopScope)

            scope.declareVariable(
                node.loopVariable.value,
                DataType.IntegerType,
                IntegerValue(i),
                node.line
            )

            val result = evaluateOther(node.block)

            popScope()

            if (result is ControlFlowResult.Continue)
                continue
            if (result is ControlFlowResult.Break)
                break
            if (result is ControlFlowResult.Value)
                returnValue = result.runtimeValue
        }

        return ControlFlowResult.Value(returnValue)
    }
}