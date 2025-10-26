package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.value.InstanceValue
import dev.vanadium.avo.syntax.ast.MemberAccessNode

@ExpressionInterpreterImplementation
class MemberAccessInterpreter(runtime: Runtime) : ExpressionInterpreter<MemberAccessNode>(runtime) {
    override fun evaluate(node: MemberAccessNode): ControlFlowResult {
        val receiverResult = evaluateOther(node.receiver)
        if (receiverResult !is ControlFlowResult.Value)
            throw RuntimeError(
                "Member access receiver may not evaluate to a ${receiverResult.name()}",
                node.line
            )

        val receiver = receiverResult.runtimeValue

        if (receiver !is InstanceValue)
            throw RuntimeError(
                "Member access is only permitted on complex type instances, attempted on ${receiver.dataType()}",
                node.line
            )

        val memberId = node.member
        val member = receiver.fields[memberId.value] ?: throw RuntimeError(
            "Instance of type ${receiver.name()} does not have a member ${memberId.asIdentifier()}",
            node.line
        )

        return ControlFlowResult.Value(member)
    }
}