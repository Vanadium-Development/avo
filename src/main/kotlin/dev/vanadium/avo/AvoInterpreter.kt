package dev.vanadium.avo

import com.google.gson.Gson
import dev.vanadium.avo.logging.DefaultLogger
import dev.vanadium.avo.logging.Logger
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.ExpressionNode
import dev.vanadium.avo.syntax.ast.ProgramNode
import dev.vanadium.avo.syntax.lexer.Lexer
import dev.vanadium.avo.syntax.parser.Parser
import java.io.File

class AvoInterpreterBuilder {
    private var sourceCode: String? = null
    private var loggerImpl: Logger = DefaultLogger.NoLogger

    val source get() = sourceCode
    val logger get() = loggerImpl

    fun logger(logger: () -> Logger) {
        this.loggerImpl = logger()
    }

    fun sourceFile(sourceFile: () -> File) {
        val file = sourceFile()
        if (!file.exists())
            throw RuntimeException("Source file does not exist: ${file.path}")

        sourceCode = file.readText(Charsets.UTF_8)
    }

    fun sourcePath(sourcePath: () -> String) {
        val file = File(sourcePath())
        if (!file.exists())
            throw RuntimeException("Source file does not exist: $sourcePath")

        sourceCode = file.readText(Charsets.UTF_8)
    }

    fun source(source: () -> String) {
        this.sourceCode = source()
    }
}

fun Avo(block: AvoInterpreterBuilder.() -> Unit): AvoInterpreter {
    val builder = AvoInterpreterBuilder()
    builder.block()
    val src = builder.source ?: throw RuntimeException("No source set")
    return AvoInterpreter(src, builder.logger)
}

class AvoInterpreter(val source: String, val logger: Logger) {
    private val lexer = Lexer(source)
    private val parser = Parser(lexer)
    private val program: ProgramNode = parser.parse()
    private val interpreter = Interpreter()
    private val gson = Gson().newBuilder().setPrettyPrinting().create()

    fun run() {
        program.nodes.forEach f@{
            if (it !is ExpressionNode)
                return@f

            val expr = interpreter.evaluate(it)
            println(gson.toJson(expr))
        }
    }
}