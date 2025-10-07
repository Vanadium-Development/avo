package org.vanadium.avo

import org.vanadium.avo.syntax.lexer.Lexer
import java.nio.file.Files
import kotlin.io.path.Path

fun main() {
    val str = Files.readString(Path("grammar.avo"))
    val l = Lexer(str)
    println("Test")
    while (l.hasNext()) {
        val tok = l.nextToken()
        println(tok)
    }
}