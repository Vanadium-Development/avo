package org.vanadium.avo.runtime.interpreter.expression

import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.VariableReferenceNode

class VariableReferenceInterpreter(interpreter: Interpreter) :
    ExpressionInterpreter<VariableReferenceNode>(interpreter) {

    override fun evaluate(node: VariableReferenceNode): RuntimeValue {
        return scope.getVariable(node.identifier.value).value
    }
}