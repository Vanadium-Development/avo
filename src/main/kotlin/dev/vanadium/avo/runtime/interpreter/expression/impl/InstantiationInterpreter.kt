package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.InstanceValue
import dev.vanadium.avo.runtime.types.value.RuntimeValue
import dev.vanadium.avo.syntax.ast.InstantiationNode
import dev.vanadium.avo.util.findFirstDuplicate

@ExpressionInterpreterImplementation
class InstantiationInterpreter(runtime: Runtime) :
    ExpressionInterpreter<InstantiationNode>(runtime) {
    override fun evaluate(node: InstantiationNode): ControlFlowResult {
        val type = scope.getComplexType(node.typeIdentifier.value, node.typeIdentifier.line)

        // Number of fields must match
        if (type.fields.size != node.fields.size)
            throw RuntimeError(
                "Got ${node.fields.size} fields on instantiation of type ${node.typeIdentifier.asIdentifier()} which is defined with ${type.fields.size} fields.",
                node.line
            )

        // Check if there are duplicate fields in the instantiation expression
        if (node.fields.map { it.identifier.value }.findFirstDuplicate() != null)
            throw RuntimeError(
                "Encountered duplicate field names in instantiation of complex type ${node.typeIdentifier}",
                node.typeIdentifier.line
            )

        val expectedFields = type.fields.associate { it.identifier.value to it.dataType }
        val instanceFields = hashMapOf<String, RuntimeValue>()

        node.fields.forEach { field ->
            // Evaluate instantiation field
            val result = runtime.evaluate(field.expression)
            if (result !is ControlFlowResult.Value)
                throw RuntimeError(
                    "Instantiation field of ${node.typeIdentifier.asIdentifier()} may not evaluate to a ${result.name()}",
                    node.line
                )

            // Check if field is valid in the instantiated type
            if (field.identifier.value !in expectedFields.keys)
                throw RuntimeError(
                    "Unknown field \"${field.identifier.value}\" in instantiation of complex type ${node.typeIdentifier}",
                    node.line
                )

            val resultValue = result.runtimeValue
            val fieldIdentifier = field.identifier
            val resultType = resultValue.dataType()

            // Check if expression type matches the defined field type
            val expectedFieldType = expectedFields[fieldIdentifier.value]!!
            if (expectedFieldType != resultValue.dataType())
                throw RuntimeError(
                    "Field ${fieldIdentifier.asIdentifier()} of type ${node.typeIdentifier.asIdentifier()} is "
                    + "of type $expectedFieldType and cannot be initialized with a value of type $resultType",
                    fieldIdentifier.line
                )

            instanceFields[fieldIdentifier.value] = resultValue
        }

        return ControlFlowResult.Value(
            InstanceValue(
                type,
                instanceFields
            )
        )
    }
}