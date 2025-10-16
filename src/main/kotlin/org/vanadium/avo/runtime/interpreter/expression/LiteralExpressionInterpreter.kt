package org.vanadium.avo.runtime.interpreter.expression

import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.LiteralNode

class LiteralExpressionInterpreter(interpreter: Interpreter) : ExpressionInterpreter<LiteralNode>(interpreter) {

    override fun evaluate(node: LiteralNode): RuntimeValue = node.runtimeValue()

}