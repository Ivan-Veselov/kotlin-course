package ru.spbau.mit

import org.hamcrest.CoreMatchers
import org.junit.Assert.*
import org.junit.Test
import ru.spbau.mit.ast.*

class InterpretationTest : TestClass() {
    private fun testAstInterpretation(ast: AstFile, expectedOutput: String) {
        val actualOutput = executeAst(ast)
        assertThat(actualOutput, CoreMatchers.equalTo(expectedOutput))
    }

    @Test
    fun testGcd() {
        val functionName = "gcd"

        fun buildFunctionDefinition() : AstFunctionDefinition {
            val firstArgumentName = "a"
            val secondArgumentName = "b"

            fun buildFunctionBody() : AstBlock {
                fun buildIf() : AstIf {
                    fun buildCondition(): AstExpression {
                        return AstBinaryExpression(
                            BinaryOperationType.EQ,
                            AstVariableAccess(secondArgumentName, 0, null),
                            AstLiteral(0, 0, null),
                            0,
                            null
                        )
                    }

                    fun buildThenBody(): AstBlock {
                        return AstBlock(listOf(
                            AstReturn(
                                AstVariableAccess(firstArgumentName, 0, null),
                                0,
                                null
                            )
                        ))
                    }

                    fun buildElseBody(): AstBlock {
                        fun buildFunctionCall() : AstFunctionCall {
                            fun buildSecondArgument() : AstExpression {
                                return AstBinaryExpression(
                                    BinaryOperationType.REM,
                                    AstVariableAccess(firstArgumentName, 0, null),
                                    AstVariableAccess(secondArgumentName, 0, null),
                                    0,
                                    null
                                )
                            }

                            return AstFunctionCall(
                                functionName,
                                listOf(
                                    AstVariableAccess(secondArgumentName, 0, null),
                                    buildSecondArgument()
                                ),
                                0,
                                null
                            )
                        }

                        return AstBlock(listOf(
                            AstReturn(buildFunctionCall(), 0, null)
                        ))
                    }

                    return AstIf(
                        buildCondition(),
                        buildThenBody(),
                        buildElseBody(),
                        0,
                        null)
                }

                return AstBlock(listOf(buildIf()))
            }

            return AstFunctionDefinition(
                functionName,
                listOf(firstArgumentName, secondArgumentName),
                buildFunctionBody(),
                0,
                null
            )
        }

        fun buildFunctionCall() : AstFunctionCall {
            return AstFunctionCall(BuiltinsHandler.printlnName, listOf(AstFunctionCall(
                functionName,
                listOf(
                    AstLiteral(24157817, 0, null),
                    AstLiteral(39088169, 0, null)
                ),
                0,
                null
            )), 0, null)
        }

        val ast = AstFile(AstBlock(listOf(
            buildFunctionDefinition(),
            buildFunctionCall()
        )))

        testAstInterpretation(ast, "1\n")
    }
}
