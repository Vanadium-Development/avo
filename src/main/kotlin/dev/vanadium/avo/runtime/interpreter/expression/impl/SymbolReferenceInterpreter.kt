package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.symbol.Function
import dev.vanadium.avo.runtime.types.symbol.Namespace
import dev.vanadium.avo.runtime.types.symbol.Variable
import dev.vanadium.avo.runtime.types.value.LambdaValue
import dev.vanadium.avo.runtime.types.value.NamespaceValue
import dev.vanadium.avo.syntax.ast.SymbolReferenceNode

@ExpressionInterpreterImplementation
class SymbolReferenceInterpreter(runtime: Runtime) :
    ExpressionInterpreter<SymbolReferenceNode>(runtime) {
    override fun evaluate(node: SymbolReferenceNode): ControlFlowResult {
        return when (val symbol = scope.getSymbol(node.identifier.value, node.line)) {
            is Variable -> ControlFlowResult.Value(symbol.value)
            is Function -> ControlFlowResult.Value(LambdaValue(symbol))
            is Namespace -> ControlFlowResult.Value(NamespaceValue(symbol))
        }
    }
}