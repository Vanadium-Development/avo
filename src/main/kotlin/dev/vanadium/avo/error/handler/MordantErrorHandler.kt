package dev.vanadium.avo.error.handler

import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.rendering.TextAlign
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import dev.vanadium.avo.error.RuntimeError
import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.error.SyntaxError

object MordantErrorHandler : DefaultErrorHandler(), ErrorHandler {
    private val terminal = Terminal()

    private fun error(
        title: String,
        subtitle: String,
        message: String
    ) {
        val table = table {
            borderType = BorderType.Companion.DOUBLE
            borderStyle = TextColors.red
            align = TextAlign.LEFT
            body {
                cellBorders = Borders.NONE
                row {
                    cell("($title)") {
                        style = TextColors.red
                        cellBorders = Borders.LEFT
                    }
                    cell(message) {
                        style = TextColors.white
                        align = TextAlign.RIGHT
                    }
                }
                row(subtitle) {
                    style = TextColors.Companion.rgb("#95a5a6")
                    cellBorders = Borders.LEFT
                }
            }
        }
        terminal.println(table)
    }

    override fun syntaxError(error: SyntaxError) {
        error("Syntax Error", "Line ${error.line}", error.message)
    }

    override fun runtimeError(error: RuntimeError) {
        error("Runtime Error", "Line ${error.line}", error.message)
    }

    override fun sourceError(error: SourceError) {
        error("Source Error", "File ${error.file}", error.message)
    }
}