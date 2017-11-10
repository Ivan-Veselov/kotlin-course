package ru.spbau.mit

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class IntegrationTest : TestClass() {
    private fun testSourceCode(sourceFileName: String, expectedOutput: String) {
        val sourceCode = getFileContent(sourceFileName)

        val byteArray = ByteArrayOutputStream()
        PrintStream(byteArray).use {
            buildAst(sourceCode).body.execute(Context(BuiltinsHandler(it)))
            it.flush()
            val actualOutput = byteArray.toString()

            assertThat(actualOutput, equalTo(expectedOutput))
        }
    }

    @Test
    fun testSimple() {
        testSourceCode("/simple.fun", "0\n")
    }

    @Test
    fun testFib() {
        testSourceCode("/fib.fun", "1 1\n2 2\n3 3\n4 5\n5 8\n")
    }

    @Test
    fun testCurrying() {
        testSourceCode("/currying.fun", "42\n")
    }
}