package ru.spbau.mit

import com.google.common.collect.ImmutableList
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
                        return AstBlock(ImmutableList.of(
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
                                ImmutableList.of(
                                    AstVariableAccess(secondArgumentName),
                                    buildSecondArgument()
                                )
                            )
                        }

                        return AstBlock(ImmutableList.of(
                            AstReturn(buildFunctionCall())
                        ))
                    }

                    return AstIf(buildCondition(), buildThenBody(), buildElseBody())
                }

                return AstBlock(ImmutableList.of(buildIf()))
            }

            return AstFunctionDefinition(
                functionName,
                ImmutableList.of(firstArgumentName, secondArgumentName),
                buildFunctionBody()
            )
        }

        fun buildFunctionCall() : AstFunctionCall {
            return AstFunctionCall(BuiltinsHandler.printlnName, ImmutableList.of(AstFunctionCall(
                functionName,
                ImmutableList.of(AstLiteral(24157817), AstLiteral(39088169))
            )))
        }

        val ast = AstFile(AstBlock(ImmutableList.of(
            buildFunctionDefinition(),
            buildFunctionCall()
        )))

        testAstInterpretation(ast, "1\n")
    }
}
