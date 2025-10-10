package org.vanadium.avo

import com.google.gson.Gson
import org.vanadium.avo.runtime.Interpreter
import org.vanadium.avo.runtime.Scope
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
    val interpreter = Interpreter(Scope(null))
    println(Gson().newBuilder().setPrettyPrinting().create().toJson(ast))
    println("==> ${interpreter.eval(ast.nodes.get(0) as ExpressionNode)}")

}