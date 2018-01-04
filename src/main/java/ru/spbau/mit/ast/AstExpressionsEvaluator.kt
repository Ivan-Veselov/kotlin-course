package ru.spbau.mit.ast

import ru.spbau.mit.BuiltinsHandler
import ru.spbau.mit.Context
import ru.spbau.mit.ast.visitors.AstExpressionsVisitor

class AstExpressionsEvaluator(
    private val context: Context.FixedContext,
    private val listener: ExecutionListener?
) : AstExpressionsVisitor<Int> {
    override suspend fun visit(node: AstVariableAccess): Int {
        return context.getVariable(node.identifier).data
    }

    override suspend fun visit(node: AstFunctionCall): Int {
        val defaultReturnValue = 0

        if (node.identifier == BuiltinsHandler.printlnName) {
            context.builtinsHandler.println(node.argumentExpressions.map { it.accept(this) })
            return defaultReturnValue
        }

        val function = context.getFunction(node.identifier)

        val functionContext = Context(function.initialContext)
        functionContext.addFunction(node.identifier, function)

        if (function.argumentNames.size != node.argumentExpressions.size) {
            throw WrongNumberOfFunctionArgumentsException(node.identifier)
        }

        val argumentsNumber = function.argumentNames.size
        repeat(argumentsNumber) {
            functionContext.addVariable(
                    function.argumentNames[it],
                    Variable(node.argumentExpressions[it].accept(this))
            )
        }

        val result = function.body.accept(AstNodesExecutor(functionContext, listener))
        return if (result.unwind) result.value else defaultReturnValue
    }

    override suspend fun visit(node: AstLiteral): Int {
        return node.value
    }

    override suspend fun visit(node: AstBinaryExpression): Int {
        val leftValue = node.leftOperand.accept(this)
        val rightValue = node.rightOperand.accept(this)

        return node.operationType.evaluate(leftValue, rightValue)
    }
}