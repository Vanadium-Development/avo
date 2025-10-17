package dev.vanadium.avo.runtime.interpreter.expression

import dev.vanadium.avo.exception.AvoRuntimeException
import dev.vanadium.avo.runtime.interpreter.types.RuntimeValue
import dev.vanadium.avo.runtime.interpreter.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.runtime.interpreter.types.ControlFlowResult
import dev.vanadium.avo.syntax.ast.VariableDeclarationNode
import dev.vanadium.avo.runtime.interpreter.types.DataType

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

    override fun evaluate(node: VariableDeclarationNode): ControlFlowResult {
        var expr: RuntimeValue

        if (node.value != null) {
            val exprResult = evaluateOther(node.value)
            if (exprResult !is ControlFlowResult.Value)
                throw AvoRuntimeException(
                    "Variable declaration value cannot evaluate to a ${exprResult.name()}"
                )
            expr = exprResult.runtimeValue
        } else {
            expr = node.type.variableDefaultValue()
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

        return ControlFlowResult.Value(expr)
    }
}