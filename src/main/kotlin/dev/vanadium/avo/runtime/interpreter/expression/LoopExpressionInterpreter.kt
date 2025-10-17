package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.Scope
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.syntax.ast.LoopExpressionNode
import dev.vanadium.avo.runtime.interpreter.types.DataType

class LoopExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<LoopExpressionNode>(interpreter) {
    override fun evaluate(node: LoopExpressionNode): ControlFlowResult {
        val lowerBoundResult = evaluateOther(node.start.expression)
        val upperBoundResult = evaluateOther(node.end.expression)
        val stepSizeResult = evaluateOther(node.step)

        if (lowerBoundResult !is ControlFlowResult.Value)
            throw AvoRuntimeException(
                "Lower bound of loop cannot evaluate to ${lowerBoundResult.name()}"
            )

        if (upperBoundResult !is ControlFlowResult.Value)
            throw AvoRuntimeException(
                "Upper bound of loop cannot evaluate to ${upperBoundResult.name()}"
            )

        if (stepSizeResult !is ControlFlowResult.Value)
            throw AvoRuntimeException(
                "Step size of loop cannot evaluate to ${stepSizeResult.name()}"
            )

        val lowerBound = lowerBoundResult.runtimeValue

        if (lowerBound.dataType() != DataType.IntegerType)
            throw AvoRuntimeException(
                "Lower bound of loop is an integer: ${lowerBound.dataType()}"
            )

        val upperBound = upperBoundResult.runtimeValue

        if (upperBound.dataType() != DataType.IntegerType)
            throw AvoRuntimeException(
                "Upper bound of loop is not an integer: ${upperBound.dataType()}"
            )

        val stepSize = stepSizeResult.runtimeValue

        if (stepSize.dataType() != DataType.IntegerType)
            throw AvoRuntimeException(
                "Step size of loop is not an integer: ${stepSize.dataType()}"
            )

        val start = (lowerBound as RuntimeValue.IntegerValue).value
        val end = (upperBound as RuntimeValue.IntegerValue).value
        val stepInt = (stepSize as RuntimeValue.IntegerValue).value

        if (start > end)
            throw AvoRuntimeException(
                "Upper loop bound must be greater than the lower bound: $start -> $end"
            )

        if (stepInt <= 0)
            throw AvoRuntimeException(
                "Step size of loop must be > 0, got $stepInt"
            )

        val actualStart = if (node.start.exclusive) start + 1 else start
        val actualEnd = if (node.end.exclusive) end - 1 else end
        val loop = actualStart..actualEnd step stepInt

        var returnValue: RuntimeValue = RuntimeValue.VoidValue()

        for (i in loop) {
            val loopScope = Scope(scope)
            pushScope(loopScope)

            scope.declareVariable(
                node.loopVariable.value,
                DataType.IntegerType,
                RuntimeValue.IntegerValue(i)
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