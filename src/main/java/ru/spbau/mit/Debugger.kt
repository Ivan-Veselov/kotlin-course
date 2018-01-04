package ru.spbau.mit

import kotlinx.coroutines.experimental.runBlocking
import ru.spbau.mit.ast.AstExpression
import ru.spbau.mit.ast.AstFile
import ru.spbau.mit.ast.AstNodesExecutor
import java.io.PrintStream
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.CoroutineContext
import kotlin.coroutines.experimental.EmptyCoroutineContext
import kotlin.coroutines.experimental.startCoroutine

class DebuggerIsNotRunningException : Exception()

class Debugger(sourceCode: String, private val stream: PrintStream) {
    private val listener: BreakpointsListener = BreakpointsListener()

    private val file: AstFile = buildAst(sourceCode)

    private var isRunning: Boolean = false

    fun addBreakpoint(line: Int) {
        listener.registerBreakpoint(line, SimpleBreakpoint())
    }

    fun addConditionalBreakpoint(line: Int, conditionalExpression: AstExpression) {
        listener.registerBreakpoint(line, ConditionalBreakpoint(conditionalExpression))
    }

    fun listBreakpoints() : List<Pair<Int, Breakpoint>> {
        return listener.listBreakpoints()
    }

    fun removeBreakpoint(line: Int) {
        listener.unregisterBreakpoint(line)
    }

    fun runInterpretation() {
        isRunning = true

        val action: suspend () -> Unit = {
            file.accept(AstNodesExecutor(Context(BuiltinsHandler(stream)), listener))
        }

        action.startCoroutine(object : Continuation<Unit> {
            override val context: CoroutineContext = EmptyCoroutineContext

            override fun resume(value: Unit) { }

            override fun resumeWithException(exception: Throwable) { }
        })
    }

    fun evaluateExpression(expression: AstExpression) {
        if (!isRunning) {
            throw DebuggerIsNotRunningException()
        }

        val value = runBlocking {
            expression.evaluate(listener.currentContext()!!, null)
        }

        stream.println(value)
    }

    fun continueInterpretation() {
        if (!isRunning) {
            throw DebuggerIsNotRunningException()
        }

        listener.resume()
    }
}