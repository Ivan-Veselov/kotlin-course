package ru.spbau.mit

import org.apache.commons.io.FileUtils
import ru.spbau.mit.ast.AstFile
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.Paths

open class TestClass {
    protected fun getFileContent(fileName: String) : String {
        val path = Paths.get(javaClass.getResource(fileName).toURI())
        return FileUtils.readFileToString(path.toFile(), null as Charset?)
    }

    protected fun executeAst(ast: AstFile) : String {
        val byteArray = ByteArrayOutputStream()
        PrintStream(byteArray).use {
            ast.body.execute(Context(BuiltinsHandler(it)))
            it.flush()
            return byteArray.toString()
        }
    }
}