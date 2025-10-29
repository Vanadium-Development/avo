package dev.vanadium.avo.module

import dev.vanadium.avo.error.SourceError
import dev.vanadium.avo.syntax.lexer.Lexer
import dev.vanadium.avo.syntax.parser.Parser

class ModuleLoader(val sourceScanner: SourceScanner) {
    private val modules: HashMap<String, Module> = hashMapOf()

    fun loadAllModules() {
        modules.clear()

        val sources = sourceScanner.findSourceFiles()
        if (sources.isEmpty())
            return

        sources.forEach { f ->
            val src = f.readText(Charsets.UTF_8)
            val module = Parser(Lexer(src)).parse()
            if (modules[module.name] != null)
                throw SourceError("Duplicate module definition: ", f.name)
            modules[module.name] = Module(f, module)
        }
    }

    fun findMainModule(): Module {
        return modules["main"] ?: throw SourceError(
            "Main module not found. Found modules: [${modules.keys.joinToString(", ")}]",
            "Tree"
        )
    }

    fun findModule(
        name: String,
        importingModule: Module
    ): Module {
        return modules[name] ?: throw SourceError(
            "Undefined module: $name",
            importingModule.path.name
        )
    }

}