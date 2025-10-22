package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.interpreter.InterpreterImpl
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.LiteralNode

@InterpreterImpl
class LiteralExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<LiteralNode>(interpreter) {
    override fun evaluate(node: LiteralNode): ControlFlowResult = ControlFlowResult.Value(node.runtimeValue())
}