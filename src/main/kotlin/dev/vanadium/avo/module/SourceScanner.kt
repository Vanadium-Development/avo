package dev.vanadium.avo.module

import dev.vanadium.avo.util.Constants
import java.io.File

class SourceScanner(val root: File) {

    fun findSourceFiles(): List<File> = root.walk().filter {
        it.isFile && it.name.endsWith(".${Constants.SOURCE_EXTENSION}")
    }.toList()

}