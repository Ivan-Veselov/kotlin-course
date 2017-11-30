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
                            AstVariableAccess(secondArgumentName),
                            AstLiteral(0)
                        )
                    }

                    fun buildThenBody(): AstBlock {
                        return AstBlock(listOf(
                            AstReturn(AstVariableAccess(firstArgumentName))
                        ))
                    }

                    fun buildElseBody(): AstBlock {
                        fun buildFunctionCall() : AstFunctionCall {
                            fun buildSecondArgument() : AstExpression {
                                return AstBinaryExpression(
                                    BinaryOperationType.REM,
                                    AstVariableAccess(firstArgumentName),
                                    AstVariableAccess(secondArgumentName)
                                )
                            }

                            return AstFunctionCall(
                                functionName,
                                listOf(
                                    AstVariableAccess(secondArgumentName),
                                    buildSecondArgument()
                                )
                            )
                        }

                        return AstBlock(listOf(
                            AstReturn(buildFunctionCall())
                        ))
                    }

                    return AstIf(buildCondition(), buildThenBody(), buildElseBody())
                }

                return AstBlock(listOf(buildIf()))
            }

            return AstFunctionDefinition(
                functionName,
                listOf(firstArgumentName, secondArgumentName),
                buildFunctionBody()
            )
        }

        fun buildFunctionCall() : AstFunctionCall {
            return AstFunctionCall(BuiltinsHandler.printlnName, listOf(AstFunctionCall(
                functionName,
                listOf(AstLiteral(24157817), AstLiteral(39088169))
            )))
        }

        val ast = AstFile(AstBlock(listOf(
            buildFunctionDefinition(),
            buildFunctionCall()
        )))

        testAstInterpretation(ast, "1\n")
    }
}
