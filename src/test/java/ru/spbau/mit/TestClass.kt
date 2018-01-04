package ru.spbau.mit

import kotlinx.coroutines.experimental.runBlocking
import org.apache.commons.io.FileUtils
import ru.spbau.mit.ast.AstFile
import ru.spbau.mit.ast.AstNodesExecutor
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.Paths

open class TestClass {
    protected fun getFile(fileName: String) : File {
        return Paths.get(javaClass.getResource(fileName).toURI()).toFile()
    }

    protected fun getFileContent(fileName: String) : String {
        return FileUtils.readFileToString(getFile(fileName), null as Charset?)
    }

    protected fun executeAst(ast: AstFile) : String {
        val byteArray = ByteArrayOutputStream()
        PrintStream(byteArray).use {
            runBlocking {
                ast.accept(AstNodesExecutor(Context(BuiltinsHandler(it)), null))
            }

            it.flush()
            return byteArray.toString()
        }
    }
}