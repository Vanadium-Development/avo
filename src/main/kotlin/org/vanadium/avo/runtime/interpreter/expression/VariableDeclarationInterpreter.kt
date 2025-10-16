package org.vanadium.avo.runtime.interpreter.expression

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.VariableDeclarationNode
import org.vanadium.avo.types.DataType

class VariableDeclarationInterpreter(interpreter: Interpreter) :
    ExpressionInterpreter<VariableDeclarationNode>(interpreter) {

    private fun DataType.variableDefaultValue() = when (this) {
        DataType.IntegerType -> RuntimeValue.IntegerValue.Companion.defaultValue()
        DataType.StringType -> RuntimeValue.StringValue.Companion.defaultValue()
        DataType.BooleanType -> RuntimeValue.BooleanValue.Companion.defaultValue()
        DataType.FloatType -> RuntimeValue.FloatValue.Companion.defaultValue()
        DataType.VoidType -> throw AvoRuntimeException(
            "Variable cannot be declared with void type"
        )

        DataType.InferredType -> throw AvoRuntimeException(
            "Cannot infer type in unassigned variable"
        )

        is DataType.LambdaType -> throw AvoRuntimeException(
            "Lambda variable must be assigned on declaration"
        )

        is DataType.ComplexType -> throw AvoRuntimeException(
            "Complex types are not supported yet"
        )
    }


    override fun evaluate(node: VariableDeclarationNode): RuntimeValue {
        val expr = if (node.value == null) {
            // If the variable was declared with no value, create a default one
            node.type.variableDefaultValue()
        } else {
            // Is a value is provided, evaluate it
            evaluateOther(node.value)
        }

        val exprType = expr.dataType()

        // TODO Implement comparison for complex types
        if (node.type == DataType.InferredType) {
            node.type = exprType
        } else if (exprType != node.type) {
            throw AvoRuntimeException(
                "Expression of variable \"${node.identifier.value}\" is of type " +
                        "$exprType but is declared with type ${node.type}"
            )
        }

        scope.declareVariable(node.identifier.value, node.type, expr)
        return expr
    }
}