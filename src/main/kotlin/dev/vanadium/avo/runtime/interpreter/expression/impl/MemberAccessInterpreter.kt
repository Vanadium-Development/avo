package dev.vanadium.avo.runtime.interpreter.expression.impl

import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.runtime.interpreter.Runtime
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreter
import dev.vanadium.avo.runtime.interpreter.expression.ExpressionInterpreterImplementation
import dev.vanadium.avo.runtime.types.ControlFlowResult
import dev.vanadium.avo.runtime.types.symbol.Function
import dev.vanadium.avo.runtime.types.symbol.Namespace
import dev.vanadium.avo.runtime.types.symbol.Variable
import dev.vanadium.avo.runtime.types.value.InstanceValue
import dev.vanadium.avo.runtime.types.value.LambdaValue
import dev.vanadium.avo.runtime.types.value.NamespaceValue
import dev.vanadium.avo.runtime.types.value.RuntimeValue
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

        return ControlFlowResult.Value(when (receiver) {
            is InstanceValue -> instanceMemberAccess(node, receiver)
            is NamespaceValue -> namespaceMemberAccess(node, receiver)
            else -> throw RuntimeError(
                "Could not perform member access on ${receiver.name()}",
                node.line
            )
        })
    }

    private fun instanceMemberAccess(
        node: MemberAccessNode,
        receiver: InstanceValue
    ): RuntimeValue {
        val memberId = node.member
        val member = receiver.fields[memberId.value] ?: throw RuntimeError(
            "Instance of type ${receiver.name()} does not have a member ${memberId.asIdentifier()}",
            node.line
        )
        return member
    }

    private fun namespaceMemberAccess(
        node: MemberAccessNode,
        receiver: NamespaceValue
    ): RuntimeValue {
        val memberId = node.member
        if (!receiver.namespace.scope.isSymbolIdentifierTaken(node.member.value))
            throw RuntimeError(
                "Namespace ${receiver.namespace.identifier} does not have a member ${memberId.asIdentifier()}",
                node.line
            )
        return when (val member = receiver.namespace.scope.getSymbol(node.member.value, node.line)) {
            is Function -> LambdaValue(member)
            is Namespace -> NamespaceValue(member)
            is Variable -> member.value
        }
    }

}