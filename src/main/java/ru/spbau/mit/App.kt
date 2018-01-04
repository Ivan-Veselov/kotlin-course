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

class InvalidNumberOfArgumentsException : Exception()

class App(
    private val inStream: BufferedReader,
    private val outStream: PrintStream,
    private val errStream: PrintStream
) {
    private var debugger: Debugger? = null

    fun run() {
        while (true) {
            try {
                val tokens = inStream.readLine()?.split(' ') ?: break
                if (tokens.isEmpty()) {
                    continue
                }

                try {
                    executeCommand(tokens)
                } catch (_: InvalidNumberOfArgumentsException) {
                    errStream.println("Invalid number of arguments")
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

    private fun executeCommand(tokens: List<String>) {
        fun assertNumberOfArguments(numberOfArguments: Int) {
            if (tokens.size != numberOfArguments + 1) {
                throw InvalidNumberOfArgumentsException()
            }
        }

        val command = tokens.first()
        val debuggerCopy = debugger

        when (command) {
            "load" -> {
                assertNumberOfArguments(1)

                debugger = Debugger(getFileContent(tokens[1]), outStream)
            }

            "breakpoint" -> {
                assertNumberOfArguments(1)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                debuggerCopy.addBreakpoint(tokens[1].toInt())
            }

            "condition" -> {
                assertNumberOfArguments(2)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                debuggerCopy.addConditionalBreakpoint(
                        tokens[1].toInt(),
                        buildExpression(tokens[2])
                )
            }

            "list" -> {
                assertNumberOfArguments(0)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                for (pair in debuggerCopy.listBreakpoints()) {
                    outStream.println("${pair.first}: ${pair.second}")
                }
            }

            "remove" -> {
                assertNumberOfArguments(1)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                debuggerCopy.removeBreakpoint(tokens[1].toInt())
            }

            "run" -> {
                assertNumberOfArguments(0)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                debuggerCopy.runInterpretation()
            }

            "evaluate" -> {
                assertNumberOfArguments(1)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                debuggerCopy.evaluateExpression(buildExpression(tokens[1]))
            }

            "stop" -> {
                assertNumberOfArguments(0)

                debugger = null
            }

            "continue" -> {
                assertNumberOfArguments(0)

                if (debuggerCopy == null) {
                    throw NoRunningProgramException()
                }

                debuggerCopy.continueInterpretation()
            }

            else -> errStream.println("Unknown command: $command")
        }
    }

    private fun getFileContent(filePath: String) : String {
        return FileUtils.readFileToString(Paths.get(filePath).toFile(), null as Charset?)
    }

    private fun buildExpression(sourceCode: String): AstExpression {
        val file = buildAst(sourceCode)
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