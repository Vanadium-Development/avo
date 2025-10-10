package org.vanadium.avo.runtime

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.runtime.RuntimeValue.*
import org.vanadium.avo.syntax.ast.*
import org.vanadium.avo.types.DataType

class Interpreter(val scope: Scope) {

    fun eval(node: ExpressionNode): RuntimeValue = when (node) {
        is BinaryOperationNode -> evalBinaryExpressionNode(node)
        is LiteralNode -> evalLiteralExpressionNode(node)
        is VariableDeclarationNode -> evalVariableDeclarationNode(node)
        is VariableReferenceNode -> evalVariableReferenceNode(node)
        is VariableAssignmentNode -> evalVariableAssignmentNode(node)
        is FunctionDefinitionNode -> evalFunctionDefinitionNode(node)
        else -> throw AvoRuntimeException(
            "Could not evaluate expression node ${node.javaClass.simpleName}"
        )
    }

    private fun evalBinaryExpressionNode(node: BinaryOperationNode): RuntimeValue {
        val left = eval(node.left)
        val right = eval(node.right)

        return when (node.type) {
            BinaryOperationType.PLUS -> left.plus(right)
            BinaryOperationType.MINUS -> left.minus(right)
            BinaryOperationType.MULTIPLY -> left.times(right)
            BinaryOperationType.DIVIDE -> left.divide(right)
            BinaryOperationType.MODULUS -> left.modulo(right)
            BinaryOperationType.POWER -> left.pow(right)
            else -> throw AvoRuntimeException("Unsupported operation type ${node.type}")
        }
    }

    private fun DataType.variableDefaultValue() = when (this) {
        DataType.IntegerType -> IntegerValue.defaultValue()
        DataType.StringType -> StringValue.defaultValue()
        DataType.BooleanType -> BooleanValue.defaultValue()
        DataType.FloatType -> FloatValue.defaultValue()
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

    private fun evalVariableDeclarationNode(node: VariableDeclarationNode): RuntimeValue {
        val expr = if (node.value == null) {
            // If the variable was declared with no value, create a default one
            node.type.variableDefaultValue()
        } else {
            // Is a value is provided, evaluate it
            eval(node.value)
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

    private fun evalVariableReferenceNode(node: VariableReferenceNode): RuntimeValue {
        return scope.getVariable(node.identifier.value).value
    }

    private fun evalVariableAssignmentNode(node: VariableAssignmentNode): RuntimeValue {
        val expr = eval(node.value)
        val exprType = expr.dataType()
        val variable = scope.getVariable(node.identifier.value)
        if (exprType != variable.type) {
            throw AvoRuntimeException(
                "Cannot assign expression of type $exprType " +
                        "to variable \"${node.identifier.value}\" of type ${variable.type}"
            )
        }
        scope.assignVariable(node.identifier.value, expr)
        return expr
    }

    private fun evalLiteralExpressionNode(node: LiteralNode): RuntimeValue = node.runtimeValue()

    private fun evalFunctionDefinitionNode(node: FunctionDefinitionNode): LambdaValue {
        val function = scope.defineFunction(
            node.identifier.value,
            node.parameters,
            node.returnType,
            node.block
        )

        return LambdaValue(function)
    }

}