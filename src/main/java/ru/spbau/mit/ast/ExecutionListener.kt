package ru.spbau.mit.ast

import ru.spbau.mit.Context

interface ExecutionListener {
    suspend fun notifyExecutionStart(node: AstStatement, context: Context.FixedContext)
}
