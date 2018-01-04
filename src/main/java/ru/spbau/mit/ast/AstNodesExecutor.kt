package ru.spbau.mit.ast

import ru.spbau.mit.BuiltinsHandler
import ru.spbau.mit.Context
import ru.spbau.mit.ast.visitors.AstNodesVisitor

class AstNodesExecutor(
    private val context: Context,
    private val listener: ExecutionListener?
) : AstNodesVisitor<ExecutionResult> {
    override suspend fun visit(node: AstFile): ExecutionResult {
        return node.body.accept(this)
    }

    override suspend fun visit(node: AstBlock): ExecutionResult {
        for (statement in node.statements) {
            val result = statement.accept(this)
            if (result.unwind) {
                return result
            }
        }

        return ExecutionResult(false)
    }

    override suspend fun visit(node: AstFunctionDefinition): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        if (node.name == BuiltinsHandler.printlnName) {
            throw PrintlnRedefinitionException()
        }

        context.addFunction(node.name, FunFunction(node.body, node.parameterNames, context.fixed()))
        return ExecutionResult(false)
    }

    override suspend fun visit(node: AstVariableDefinition): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        val initialValue = node.initializingExpression?.evaluate(context.fixed(), listener) ?: 0
        context.addVariable(node.name, Variable(initialValue))

        return ExecutionResult(false)
    }

    override suspend fun visit(node: AstWhile): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        while (node.condition.evaluate(context.fixed(), listener) != 0)  {
            val result = node.body.accept(AstNodesExecutor(Context(context.fixed()), listener))
            if (result.unwind) {
                return result
            }
        }

        return ExecutionResult(false)
    }

    override suspend fun visit(node: AstIf): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        if (node.condition.evaluate(context.fixed(), listener) != 0)  {
            return node.thenBody.accept(AstNodesExecutor(Context(context.fixed()), listener))
        } else {
            if (node.elseBody != null) {
                return node.elseBody.accept(AstNodesExecutor(Context(context.fixed()), listener))
            }
        }

        return ExecutionResult(false)
    }

    override suspend fun visit(node: AstAssignment): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        val variable = context.getVariable(node.identifier)
        variable.data = node.expression.evaluate(context.fixed(), listener)

        return ExecutionResult(false)
    }

    override suspend fun visit(node: AstReturn): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        return ExecutionResult(true, node.expression.evaluate(context.fixed(), listener))
    }

    override suspend fun visit(node: AstExpression): ExecutionResult {
        listener?.notifyExecutionStart(node, context.fixed())

        node.evaluate(context.fixed(), listener)
        return ExecutionResult(false)
    }
}
