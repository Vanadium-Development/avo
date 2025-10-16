package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.ConditionalExpressionNode

class ConditionalExpressionInterpreter(interpreter: Interpreter) :
    ExpressionInterpreter<ConditionalExpressionNode>(interpreter) {
    override fun evaluate(node: ConditionalExpressionNode): RuntimeValue {
        node.branches.first f@{
            val condition = evaluateOther(it.condition)
            if (condition !is RuntimeValue.BooleanValue)
                throw AvoRuntimeException("Conditional expression must be a boolean value.")

            if (!condition.value)
                return@f false

            return evaluateOther(it.block)
        }

        if (node.defaultBranch == null)
            return RuntimeValue.VoidValue()

        return evaluateOther(node.defaultBranch)
    }
}