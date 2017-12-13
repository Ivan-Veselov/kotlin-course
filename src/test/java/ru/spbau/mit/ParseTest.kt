package ru.spbau.mit

import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test

class ParseTest : TestClass() {
    private fun testParsingOfSourceCode(sourceFileName: String, astStringFileName: String) {
        val sourceCode = getFileContent(sourceFileName)
        val expectedAstString = getFileContent(astStringFileName)

        assertThat(buildAst(sourceCode, null).toString(), equalTo(expectedAstString))
    }

    @Test
    fun testLanguage() {
        testParsingOfSourceCode("/language.fun", "/language.ast")
    }
}