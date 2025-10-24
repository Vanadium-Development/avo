package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.DataType
import dev.vanadium.avo.runtime.types.value.BooleanValue
import dev.vanadium.avo.runtime.types.value.FloatValue
import dev.vanadium.avo.runtime.types.value.IntegerValue
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.runtime.types.value.StringValue
import dev.vanadium.avo.syntax.ast.VariableDeclarationNode

@ExpressionInterpreterImplementation
class VariableDeclarationInterpreter(runtime: Runtime) :
    ExpressionInterpreter<VariableDeclarationNode>(runtime) {
    private fun DataType.variableDefaultValue(line: Int) = when (this) {
        DataType.IntegerType    -> IntegerValue.Companion.defaultValue()
        DataType.StringType     -> StringValue.Companion.defaultValue()
        DataType.BooleanType    -> BooleanValue.Companion.defaultValue()
        DataType.FloatType      -> FloatValue.Companion.defaultValue()
        DataType.VoidType       -> throw RuntimeError(
            "Variable cannot be declared with void type",
            line
        )

        DataType.InferredType   -> throw RuntimeError(
            "Cannot infer type in unassigned variable",
            line
        )

        is DataType.LambdaType  -> throw RuntimeError(
            "Lambda variable must be assigned on declaration",
            line
        )

        is DataType.ComplexTypeReferenceNode -> throw RuntimeError(
            "Complex types are not supported yet",
            line
        )
    }

    override fun evaluate(node: VariableDeclarationNode): ControlFlowResult {
        var expr: RuntimeValue

        if (node.value != null) {
            val exprResult = evaluateOther(node.value)
            if (exprResult !is ControlFlowResult.Value)
                throw RuntimeError(
                    "Variable declaration value cannot evaluate to a ${exprResult.name()}",
                    node.line
                )
            expr = exprResult.runtimeValue
        } else {
            expr = node.type.variableDefaultValue(node.line)
        }

        val exprType = expr.dataType()

        // TODO Implement comparison for complex types
        if (node.type == DataType.InferredType) {
            node.type = exprType
        } else if (exprType != node.type) {
            throw RuntimeError(
                "Expression of variable \"${node.identifier.value}\" is of type " +
                "$exprType but is declared with type ${node.type}",
                node.line
            )
        }

        scope.declareVariable(node.identifier.value, node.type, expr, node.line)


        return ControlFlowResult.Value(expr)
    }
}