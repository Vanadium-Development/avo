package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.LiteralNode

class LiteralExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<LiteralNode>(interpreter) {
    override fun evaluate(node: LiteralNode): RuntimeValue = node.runtimeValue()
}