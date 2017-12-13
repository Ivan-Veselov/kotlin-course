package ru.spbau.mit

import ru.spbau.mit.ast.AstExpression
import ru.spbau.mit.ast.AstStatement
import ru.spbau.mit.ast.ExecutionListener
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine
import kotlinx.coroutines.experimental.*

class BreakpointsListener : ExecutionListener {
    private val breakpoints: MutableMap<Int, Breakpoint> = mutableMapOf()

    private var currentContinuation: Continuation<Unit>? = null

    private var currentContext: Context.FixedContext? = null

    suspend override fun notifyExecutionStart(node: AstStatement, context: Context.FixedContext) {
        val breakpoint = breakpoints[node.line] ?: return

        if (breakpoint.condition(context)) {
            suspendCoroutine<Unit> { continuation ->
                this.currentContinuation = continuation
                this.currentContext = context
            }
        }
    }

    fun currentContext() : Context.FixedContext? {
        return currentContext
    }

    fun registerBreakpoint(line: Int, breakpoint: Breakpoint) {
        breakpoints[line] = breakpoint
    }

    fun unregisterBreakpoint(line: Int) {
        breakpoints.remove(line)
    }

    fun listBreakpoints(): List<Pair<Int, Breakpoint>> {
        return breakpoints.toList()
    }

    fun resume() {
        currentContinuation!!.resume(Unit)
    }
}

interface Breakpoint {
    fun condition(context: Context.FixedContext): Boolean
}

class SimpleBreakpoint : Breakpoint {
    override fun condition(context: Context.FixedContext): Boolean {
        return true
    }

    override fun toString(): String {
        return "unconditional breakpoint"
    }
}

class ConditionalBreakpoint(
    private val expression: AstExpression
) : Breakpoint {
    override fun condition(context: Context.FixedContext): Boolean {
        val value = runBlocking {
            expression.evaluate(context)
        }

        return value == 1
    }

    override fun toString(): String {
        return "conditional breakpoint"
    }
}
