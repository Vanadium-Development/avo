package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.runtime.interpreter.InterpreterImpl
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.runtime.interpreter.types.Symbol
import dev.vanadium.avo.runtime.interpreter.types.value.LambdaValue
import dev.vanadium.avo.syntax.ast.SymbolReferenceNode

@InterpreterImpl
class SymbolReferenceInterpreter(interpreter: Interpreter) :
    ExpressionInterpreter<SymbolReferenceNode>(interpreter) {
    override fun evaluate(node: SymbolReferenceNode): ControlFlowResult {
        return when (val symbol = scope.getSymbol(node.identifier.value, node.line)) {
            is Symbol.Variable -> ControlFlowResult.Value(symbol.value)
            is Symbol.Function -> ControlFlowResult.Value(LambdaValue(symbol))
        }
    }
}