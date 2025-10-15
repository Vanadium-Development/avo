package org.vanadium.avo.runtime

import org.vanadium.avo.exception.AvoRuntimeException
import org.vanadium.avo.runtime.RuntimeValue.*
import org.vanadium.avo.syntax.ast.*
import org.vanadium.avo.types.DataType
import java.util.*

class Interpreter {
    private var scopes: Stack<Scope> = Stack()
    val scope get() = scopes.peek()

    init {
        scopes.push(Scope(null))
    }

    fun eval(node: ExpressionNode): RuntimeValue = when (node) {
        is BinaryOperationNode -> evalBinaryExpressionNode(node)
        is LiteralNode -> evalLiteralExpressionNode(node)
        is VariableDeclarationNode -> evalVariableDeclarationNode(node)
        is VariableReferenceNode -> evalVariableReferenceNode(node)
        is VariableAssignmentNode -> evalVariableAssignmentNode(node)
        is FunctionDefinitionNode -> evalFunctionDefinitionNode(node)
        is FunctionCallNode -> evalFunctionCallNode(node)
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

    private fun evalFunctionCallNode(node: FunctionCallNode): RuntimeValue {
        val function = scope.getFunctionOrLambda(node.identifier.value)
        if (function.signature.size != node.parameters.size)
            throw AvoRuntimeException(
                "Function \"${node.identifier.value}\" expected ${function.signature.size} parameters, but " +
                        "received ${node.parameters.size}"
            )

        // The usable function scope is a new child scope of the captured scope
        val functionScope = Scope(function.scope)
        scopes.push(functionScope)

        val params = function.signature.zip(node.parameters)
        params.forEachIndexed { i, param ->
            val value = eval(param.second.expression)
            if (param.first.type != value.dataType())
                throw AvoRuntimeException(
                    "Parameter \"${param.first.identifier.value}\" of function \"${node.identifier.value}\" " +
                            "is declared with type $value but received ${param.first.type}"
                )

            // Declare signature variables in the function scope
            scope.declareVariable(param.first.identifier.value, value.dataType(), value)
        }

        var returnValue: RuntimeValue? = null

        for (node in function.block.nodes) {
            if (node is ReturnStatementNode) {
                returnValue = eval(node.expression)
                break
            }
            if (node is ExpressionNode) {
                eval(node)
            }
        }

        // Leave the function scope
        scopes.pop()

        return returnValue ?: (if (function.returnType is DataType.VoidType) VoidValue()
        else throw AvoRuntimeException(
            "Function \"${node.identifier.value}\" does not return a value on all paths"
        ))
    }
}