package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.RuntimeValue
import dev.vanadium.avo.runtime.Symbol
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.SymbolReferenceNode

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