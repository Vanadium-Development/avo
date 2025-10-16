package org.vanadium.avo

import com.google.gson.Gson
import org.vanadium.avo.runtime.RuntimeValue
import org.vanadium.avo.runtime.interpreter.Interpreter
import org.vanadium.avo.syntax.ast.ExpressionNode
import org.vanadium.avo.syntax.lexer.Lexer
import org.vanadium.avo.syntax.parser.Parser
import java.nio.file.Files
import kotlin.io.path.Path

fun main() {
    val str = Files.readString(Path("grammar.avo"))
    val l = Lexer(str)
    val parser = Parser(l)
    val ast = parser.parse()
    val interpreter = Interpreter()

    val values = mutableListOf<RuntimeValue>()

    ast.nodes.forEach {
        values.add(interpreter.evaluate(it as ExpressionNode))
    }

    val v = values.last()
    print(Gson().newBuilder().setPrettyPrinting().create().toJson(v))
}