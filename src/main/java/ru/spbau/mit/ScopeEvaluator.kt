package ru.spbau.mit

import ru.spbau.mit.parser.FunBaseVisitor
import ru.spbau.mit.parser.FunParser
import java.io.PrintStream

class InternalEvaluatorError : RuntimeException()

class WrongNumberOfFunctionArgumentsException : Exception()

class PrintlnRedefinitionException : Exception()

// todo: handle exceptions from Context class
class ScopeEvaluator(
    parentContext: Context.FixedContext?,
    private val stream: PrintStream
) : FunBaseVisitor<Int?>() {
    private val context: Context = Context(parentContext)

    override fun visitFile(node: FunParser.FileContext): Int? {
        visit(node.block())
        return null
    }

    override fun visitBlock(node: FunParser.BlockContext): Int? {
        for (statement in node.statements) {
            val unwindingValue = visit(statement)
            if (unwindingValue != null) {
                return unwindingValue
            }
        }

        return null
    }

    override fun visitFunctionDefinitionStatement(
        node: FunParser.FunctionDefinitionStatementContext
    ): Int? {
        if (node.functionName.text == ScopeEvaluator.printlnName) {
            throw PrintlnRedefinitionException()
        }

        context.addFunction(node.functionName.text, Function(node, context.fixed()))
        return null
    }

    override fun visitVariableDefinitionStatement(
        node: FunParser.VariableDefinitionStatementContext
    ): Int? {
        var initialValue = 0
        if (node.initialValueExpression != null) {
            initialValue = visit(node.initialValueExpression)!!
        }

        context.addVariable(node.variableName.text, Variable(initialValue))
        return null
    }

    override fun visitExpressionStatement(node: FunParser.ExpressionStatementContext): Int? {
        visit(node.expression())
        return null
    }

    override fun visitWhileStatement(node: FunParser.WhileStatementContext): Int? {
        while (visit(node.condition) != 0)  {
            val unwindingValue = ScopeEvaluator(context.fixed(), stream).visit(node.body)
            if (unwindingValue != null) {
                return unwindingValue
            }
        }

        return null
    }

    override fun visitIfStatement(node: FunParser.IfStatementContext): Int? {
        if (visit(node.condition) != 0)  {
            return ScopeEvaluator(context.fixed(), stream).visit(node.thenBody)
        } else {
            if (node.elseBody != null) {
                return ScopeEvaluator(context.fixed(), stream).visit(node.elseBody)
            }
        }

        return null
    }

    override fun visitAssignmentStatement(node: FunParser.AssignmentStatementContext): Int? {
        val variable = context.getVariable(node.IDENTIFIER().text)
        variable.data = visit(node.expression()) ?: throw InternalEvaluatorError()

        return null
    }

    override fun visitReturnStatement(node: FunParser.ReturnStatementContext): Int? {
        return visit(node.expression())
    }

    override fun visitExpressionInParentheses(
        node: FunParser.ExpressionInParenthesesContext
    ): Int? {
        return visit(node.expression())
    }

    override fun visitVariableAccessExpression(
        node: FunParser.VariableAccessExpressionContext
    ): Int? {
        return context.getVariable(node.IDENTIFIER().text).data
    }

    override fun visitFunctionCallExpression(node: FunParser.FunctionCallExpressionContext): Int? {
        val defaultReturnValue = 0

        val functionName = node.IDENTIFIER().text
        if (functionName == ScopeEvaluator.printlnName) {
            printValues(node.arguments)
            return defaultReturnValue
        }

        val function = context.getFunction(functionName)

        val functionContext = Context(function.initialContext)
        functionContext.addFunction(functionName, function)

        if (function.argumentNames.size != node.arguments.size) {
            throw WrongNumberOfFunctionArgumentsException()
        }

        val argumentsNumber = function.argumentNames.size
        for (i in 0 until argumentsNumber) {
            functionContext.addVariable(
                function.argumentNames[i],
                Variable(visit(node.arguments[i]) ?: throw InternalEvaluatorError())
            )
        }

        return ScopeEvaluator(
            functionContext.fixed(),
                stream
        ).visit(function.body) ?: defaultReturnValue
    }

    override fun visitLiteralExpression(node: FunParser.LiteralExpressionContext): Int? {
        return Integer.parseInt(node.LITERAL().text)
    }

    override fun visitBinaryExpression(node: FunParser.BinaryExpressionContext): Int? {
        val left = visit(node.leftOperand) ?: throw InternalEvaluatorError()
        val right = visit(node.rightOperand) ?: throw InternalEvaluatorError()

        when (node.operation.text) {
            "*" -> return left * right
            "/" -> return left / right
            "%" -> return left % right
            "+" -> return left + right
            "-" -> return left - right
            "<" -> return if (left < right) 1 else 0
            ">" -> return if (left > right) 1 else 0
            "<=" -> return if (left <= right) 1 else 0
            ">=" -> return if (left >= right) 1 else 0
            "==" -> return if (left == right) 1 else 0
            "!=" -> return if (left != right) 1 else 0
            "&&" -> return if (left != 0 && right != 0) 1 else 0
            "||" -> return if (left != 0 || right != 0) 1 else 0
            else -> throw InternalEvaluatorError()
        }
    }

    private fun printValues(expressions: List<FunParser.ExpressionContext>) {
        fun printValue(expression: FunParser.ExpressionContext) {
            val value = visit(expression) ?: throw InternalEvaluatorError()
            stream.print(value)
        }

        if (expressions.isNotEmpty()) {
            printValue(expressions[0])
        }

        for (expression in expressions.subList(1, expressions.size)) {
            stream.print(' ')
            printValue(expression)
        }

        stream.println()
    }

    object ScopeEvaluator {
        val printlnName = "println"
    }
}
