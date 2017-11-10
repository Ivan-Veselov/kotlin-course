package ru.spbau.mit

import org.apache.commons.io.FileUtils
import java.nio.charset.Charset
import java.nio.file.Paths

open class TestClass {
    protected fun getFileContent(fileName: String) : String {
        val path = Paths.get(javaClass.getResource(fileName).toURI())
        val charset: Charset? = null
        return FileUtils.readFileToString(path.toFile(), charset)
    }
}