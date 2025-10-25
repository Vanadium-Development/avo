package dev.vanadium.avo.error.handler

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyle
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import dev.vanadium.avo.error.LexerError
import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.SyntaxError

object MordantErrorHandler : DefaultErrorHandler(), ErrorHandler {
    private val terminal = Terminal()

    private fun error(
        title: String,
        subtitle: String,
        message: String,
        color: TextStyle
    ) {
        val table = table {
            borderType = BorderType.HEAVY
            borderStyle = color
            align = TextAlign.LEFT
            body {
                cellBorders = Borders.NONE
                row {
                    cell(title) {
                        style = color
                        cellBorders = Borders.LEFT_TOP
                    }
                    cell(message) {
                        style = TextColors.white
                        align = TextAlign.RIGHT
                        cellBorders = Borders.TOP_RIGHT
                    }
                }
                row {
                    style = TextColors.Companion.rgb("#95a5a6")
                    cell(subtitle) {
                        cellBorders = Borders.LEFT_BOTTOM
                    }
                    cell("") {
                        cellBorders = Borders.BOTTOM_RIGHT
                    }
                }
            }
        }
        terminal.println(table)
    }

    override fun syntaxError(error: SyntaxError) {
        error("Syntax Error", "Line ${error.line}", error.message, TextColors.red)
    }

    override fun runtimeError(error: RuntimeError) {
        error("Runtime Error", "Line ${error.line}", error.message, TextColors.brightRed)
    }

    override fun sourceError(error: SourceError) {
        error("Source Error", "File ${error.file}", error.message, TextColors.brightCyan)
    }

    override fun lexerError(error: LexerError) {
        error("Lexer Error", "Line ${error.line}", error.message, TextColors.brightRed)
    }
}