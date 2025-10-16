package org.vanadium.avo.runtime.interpreter.expression

import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.Symbol
import org.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.SymbolReferenceNode

class SymbolReferenceInterpreter(interpreter: Interpreter) :
    ExpressionInterpreter<SymbolReferenceNode>(interpreter) {
    override fun evaluate(node: SymbolReferenceNode): RuntimeValue {
        val symbol = scope.getSymbol(node.identifier.value)
        return when (symbol) {
            is Symbol.Variable -> symbol.value
            is Symbol.Function -> RuntimeValue.LambdaValue(symbol)
        }
    }
}