package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.Symbol
import dev.vanadium.avo.runtime.types.value.ArrayValue
import dev.vanadium.avo.runtime.types.value.InstanceValue
import dev.vanadium.avo.runtime.types.value.IntegerValue
import dev.vanadium.avo.syntax.ast.AssignmentNode
import dev.vanadium.avo.syntax.ast.IndexAccessNode
import dev.vanadium.avo.syntax.ast.MemberAccessNode
import dev.vanadium.avo.syntax.ast.SymbolReferenceNode

@ExpressionInterpreterImplementation
class AssignmentInterpreter(runtime: Runtime) :
    ExpressionInterpreter<AssignmentNode>(runtime) {
    override fun evaluate(node: AssignmentNode): ControlFlowResult {
        val exprResult = evaluateOther(node.value)
        if (exprResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Unexpected ${exprResult.name()} in a variable assignment value",
                node.line
            )

        val expr = exprResult.runtimeValue
        val exprType = expr.dataType()

        when (val target = node.target) {
            is SymbolReferenceNode -> {
                val variable = scope.getSymbol(target.identifier.value, target.line)
                if (variable !is Symbol.Variable)
                    throw RuntimeError(
                        "Symbol \"${target.identifier.value}\" is not a variable",
                        node.line
                    )

                if (exprType != variable.type) {
                    throw RuntimeError(
                        "Cannot assign expression of type $exprType " +
                        "to variable \"${target.identifier.value}\" of type ${variable.type}",
                        node.line
                    )
                }
                scope.assignVariable(target.identifier.value, expr, node.line)
            }

            is MemberAccessNode    -> {
                val instanceResult = evaluateOther(target.receiver)
                if (instanceResult !is ControlFlowResult.Value)
                    throw RuntimeError(
                        "Invalid assignment target ${instanceResult.name()}",
                        target.receiver.line
                    )
                val instanceValue = instanceResult.runtimeValue
                if (instanceValue !is InstanceValue)
                    throw RuntimeError(
                        "Cannot assign member of ${instanceValue.name()}",
                        target.receiver.line
                    )
                val fieldName = target.member
                val field = instanceValue.fields[target.member.value] ?: throw RuntimeError(
                    "Cannot assign undefined field ${fieldName.asIdentifier()} of type ${instanceValue.name()}",
                    target.member.line
                )
                if (field.dataType() != exprType)
                    throw RuntimeError(
                        "Cannot assign value of type $exprType to field ${fieldName.asIdentifier()} of type ${field.dataType()}",
                        node.line
                    )
                instanceValue.fields[target.member.value] = expr
            }

            is IndexAccessNode     -> {
                // TODO DRY
                val targetResult = evaluateOther(target.target)

                if (targetResult !is ControlFlowResult.Value)
                    throw RuntimeError(
                        "Index access target cannot valuate to a ${targetResult.name()}",
                        node.line
                    )

                val targetValue = targetResult.runtimeValue

                if (targetValue !is ArrayValue)
                    throw RuntimeError(
                        "Cannot perform index assignment on type ${targetValue.dataType()}",
                        node.line
                    )

                if (targetValue.type.elementType != exprType)
                    throw RuntimeError(
                        "Cannot assign expression of type $exprType to element of array of type ${targetValue.type}",
                        node.line
                    )

                val indexResult = evaluateOther(target.index)

                if (indexResult !is ControlFlowResult.Value)
                    throw RuntimeError(
                        "Unexpected ${indexResult.name()} in an index expression",
                        node.line
                    )

                val indexValue = indexResult.runtimeValue

                if (indexValue !is IntegerValue)
                    throw RuntimeError(
                        "Cannot use value of type ${indexValue.dataType()} as index",
                        node.line
                    )

                val index = indexValue.value
                val arraySize = targetValue.value.size

                if (index !in 0..<arraySize)
                    throw RuntimeError(
                        "Cannot assign element at index $index in array of size $arraySize",
                        node.line
                    )

                targetValue.value[index] = expr
            }

            else                   -> {
                throw RuntimeError(
                    "Invalid assignment target",
                    node.target.line
                )
            }
        }
        return exprResult
    }
}