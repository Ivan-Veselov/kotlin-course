package ru.spbau.mit

import org.apache.commons.io.FileUtils
import ru.spbau.mit.ast.AstExpression
import ru.spbau.mit.ast.ExecutionListener
import ru.spbau.mit.ast.PrintlnRedefinitionException
import ru.spbau.mit.ast.WrongNumberOfFunctionArgumentsException
import java.io.BufferedReader
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.Paths

class NoRunningProgramException : Exception()

class NotAnExpressionException : Exception()

class App(
    private val inStream: BufferedReader,
    private val outStream: PrintStream,
    private val errStream: PrintStream
) {
    fun run() {
        var debugger: Debugger? = null

        while (true) {
            try {
                val tokens = inStream.readLine()?.split(' ') ?: break
                if (tokens.isEmpty()) {
                    continue
                }

                fun assertNumberOfArguments(numberOfArguments: Int) : Boolean {
                    if (tokens.size != numberOfArguments + 1) {
                        errStream.println("Invalid number of arguments")
                        return false
                    }

                    return true
                }

                val command = tokens.first()

                when (command) {
                    "load" -> if (assertNumberOfArguments(1)) {
                        debugger = Debugger(getFileContent(tokens[1]), outStream)
                    }

                    "breakpoint" -> if (assertNumberOfArguments(1)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        debugger.addBreakpoint(tokens[1].toInt())
                    }

                    "condition" -> if (assertNumberOfArguments(2)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        debugger.addConditionalBreakpoint(
                                tokens[1].toInt(),
                                buildExpression(tokens[2], null)
                        )
                    }

                    "list" -> if (assertNumberOfArguments(0)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        for (pair in debugger.listBreakpoints()) {
                            outStream.println("${pair.first}: ${pair.second}")
                        }
                    }

                    "remove" -> if (assertNumberOfArguments(1)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        debugger.removeBreakpoint(tokens[1].toInt())
                    }

                    "run" -> if (assertNumberOfArguments(0)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        debugger.runInterpretation()
                    }

                    "evaluate" -> if (assertNumberOfArguments(1)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        debugger.evaluateExpression(buildExpression(tokens[1], null))
                    }

                    "stop" -> if (assertNumberOfArguments(0)) {
                        debugger = null
                    }

                    "continue" -> if (assertNumberOfArguments(0)) {
                        if (debugger == null) {
                            throw NoRunningProgramException()
                        }

                        debugger.continueInterpretation()
                    }

                    else -> errStream.println("Unknown command: $command")
                }
            } catch (e: FailedToParseException) {
                // all messages were written by antlr
            } catch (e: ContextSymbolOverwritingException) {
                errStream.println("Symbol overwriting: " + e.symbol)
                debugger = null
            } catch (e: ContextUndefinedSymbolException) {
                errStream.println("Undefined symbol: " + e.symbol)
                debugger = null
            } catch (e: WrongNumberOfFunctionArgumentsException) {
                errStream.println("Invalid number of arguments when calling " + e.functionName)
                debugger = null
            } catch (e: PrintlnRedefinitionException) {
                errStream.println("Attempt to redefine builtin println function")
                debugger = null
            } catch (_: NumberFormatException) {
                errStream.println("Invalid integer argument")
            } catch (_: NotAnExpressionException) {
                errStream.println("Not an expression")
            } catch (_: DebuggerIsNotRunningException) {
                errStream.println("Debugger is not running")
            } catch (_: NoRunningProgramException) {
                errStream.println("No running program")
            }
        }
    }

    private fun getFileContent(filePath: String) : String {
        return FileUtils.readFileToString(Paths.get(filePath).toFile(), null as Charset?)
    }

    private fun buildExpression(sourceCode: String, listener: ExecutionListener?): AstExpression {
        val file = buildAst(sourceCode, listener)
        val statements = file.body.statements

        if (statements.size != 1) {
            throw NotAnExpressionException()
        }

        val statement = statements.first()
        if (statement is AstExpression) {
            return statement
        }

        throw NotAnExpressionException()
    }
}