package dev.vanadium.avo

import com.google.gson.Gson
import dev.vanadium.avo.error.BaseError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.handler.ErrorHandlingConfig
import dev.vanadium.avo.runtime.interpreter.Interpreter
import dev.vanadium.avo.syntax.ast.ExpressionNode
import dev.vanadium.avo.syntax.ast.ProgramNode
import dev.vanadium.avo.syntax.lexer.Lexer
import dev.vanadium.avo.syntax.parser.Parser
import java.io.File

class AvoInterpreterBuilder {
    private var sourceCode: String? = null

    val source get() = sourceCode

    fun errorHandling(config: ErrorHandlingConfig.() -> Unit) {
        config(ErrorHandlingConfig)
    }

    fun sourceFile(sourceFile: () -> File) {
        val file = sourceFile()
        if (!file.exists())
            throw SourceError("Source file does not exist", file.path)

        sourceCode = file.readText(Charsets.UTF_8)
    }

    fun sourcePath(sourcePath: () -> String) {
        val path = sourcePath()
        val file = File(path)
        if (!file.exists())
            throw SourceError("Source file does not exist", path)

        sourceCode = file.readText(Charsets.UTF_8)
    }

    fun source(source: () -> String) {
        this.sourceCode = source()
    }
}

fun Interpreter(block: AvoInterpreterBuilder.() -> Unit): AvoInterpreter? {
    val builder = AvoInterpreterBuilder()
    try {
        builder.block()
    } catch (e: BaseError) {
        ErrorHandlingConfig.handler.dispatch(e)
        return null
    }
    val src = builder.source ?: throw SourceError("No source set", "N/A")
    return AvoInterpreter(src)
}

class AvoInterpreter(
    source: String
) {
    private val lexer = Lexer(source)
    private val parser = Parser(lexer)
    private val program: ProgramNode = parser.parse()
    private val interpreter = Interpreter()
    private val gson = Gson().newBuilder().setPrettyPrinting().create()

    val errorHandler get() = ErrorHandlingConfig.handler

    fun run() {
        try {
            program.nodes.forEach f@{
                if (it !is ExpressionNode)
                    return@f

                val expr = interpreter.evaluate(it)
                println(gson.toJson(expr))
            }
        } catch (e: BaseError) {
            errorHandler.dispatch(e)
        }
    }
}

fun AvoInterpreter?.exists(fn: AvoInterpreter.() -> Unit): AvoInterpreter? {
    if (this == null)
        return this

    fn(this)
    return this
}

fun AvoInterpreter?.notExists(fn: () -> Unit): AvoInterpreter? {
    if (this != null)
        return this

    fn()
    return this
}
