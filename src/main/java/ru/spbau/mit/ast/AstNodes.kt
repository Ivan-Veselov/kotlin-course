package ru.spbau.mit.ast

import com.google.common.collect.ImmutableList
import ru.spbau.mit.*
import ru.spbau.mit.Context.FixedContext

import ru.spbau.mit.ast.BinaryOperationType.*
import ru.spbau.mit.parser.FunParser

// todo: wrapper for Int to convert them to Boolean

// todo: handle exceptions from Context class

data class File(val body: Block) {
    companion object {
        fun buildFromRuleContext(rule: FunParser.FileContext): File {
            return File(Block.buildFromRuleContext(rule.block()))
        }
    }
}

abstract class ExecutableAstNode {
    abstract fun execute(context: Context): ExecutionResult
}

data class Block(private val statements: ImmutableList<Statement>) : ExecutableAstNode() {
    override fun execute(context: Context): ExecutionResult {
        for (statement in statements) {
            val result = statement.execute(context)
            if (result.unwind) {
                return result
            }
        }

        return ExecutionResult(false)
    }

    companion object {
        fun buildFromRuleContext(rule: FunParser.BlockContext): Block {
            return Block(ImmutableList.copyOf(
                rule.statements.map { Statement.buildFromRuleContext(it) }
            ))
        }
    }
}

abstract class Statement : ExecutableAstNode() {
    companion object {
        fun buildFromRuleContext(rule: FunParser.StatementContext) : Statement {
            return rule.accept(StatementContextVisitor)
        }
    }
}

data class FunctionDefinition(
    private val name: String,
    private val parameterNames: ImmutableList<String>,
    private val body: Block
) : Statement() {
    override fun execute(context: Context): ExecutionResult {
        if (name == BuiltinsHandler.printlnName) {
            throw PrintlnRedefinitionException()
        }

        context.addFunction(name, Function(body, parameterNames, context.fixed()))
        return ExecutionResult(false)
    }
}

data class VariableDefinition(
    private val name: String,
    private val initializingExpression: Expression?
) : Statement() {
    override fun execute(context: Context): ExecutionResult {
        var initialValue = 0
        if (initializingExpression != null) {
            initialValue = initializingExpression.evaluate(context.fixed())
        }

        context.addVariable(name, Variable(initialValue))
        return ExecutionResult(false)
    }
}

abstract class Expression : Statement() {
    abstract fun evaluate(context: FixedContext): Int

    override fun execute(context: Context): ExecutionResult {
        evaluate(context.fixed())
        return ExecutionResult(false)
    }

    companion object {
        fun buildFromRuleContext(rule: FunParser.ExpressionContext) : Expression {
            return rule.accept(ExpressionContextVisitor)
        }
    }
}

data class While(private val condition: Expression, private val body: Block) : Statement() {
    override fun execute(context: Context): ExecutionResult {
        while (condition.evaluate(context.fixed()) != 0)  {
            val result = body.execute(Context(context.fixed()))
            if (result.unwind) {
                return result
            }
        }

        return ExecutionResult(false)
    }
}

data class If(
    private val condition: Expression,
    private val thenBody: Block,
    private val elseBody: Block?
) : Statement() {
    override fun execute(context: Context): ExecutionResult {
        if (condition.evaluate(context.fixed()) != 0)  {
            return thenBody.execute(Context(context.fixed()))
        } else {
            if (elseBody != null) {
                return elseBody.execute(Context(context.fixed()))
            }
        }

        return ExecutionResult(false)
    }
}

data class Assignment(
    private val identifier: String,
    private val expression: Expression
) : Statement() {
    override fun execute(context: Context): ExecutionResult {
        val variable = context.getVariable(identifier)
        variable.data = expression.evaluate(context.fixed())

        return ExecutionResult(false)
    }
}

data class Return(private val expression: Expression) : Statement() {
    override fun execute(context: Context): ExecutionResult {
        return ExecutionResult(true, expression.evaluate(context.fixed()))
    }
}

data class VariableAccess(private val identifier: String) : Expression() {
    override fun evaluate(context: FixedContext): Int {
        return context.getVariable(identifier).data
    }
}

data class FunctionCall(
    private val identifier: String,
    private val argumentExpressions: ImmutableList<Expression>
) : Expression() {
    override fun evaluate(context: FixedContext): Int {
        val defaultReturnValue = 0

        if (identifier == BuiltinsHandler.printlnName) {
            context.builtinsHandler.println(argumentExpressions.map { it.evaluate(context) })
            return defaultReturnValue
        }

        val function = context.getFunction(identifier)

        val functionContext = Context(function.initialContext)
        functionContext.addFunction(identifier, function)

        if (function.argumentNames.size != argumentExpressions.size) {
            throw WrongNumberOfFunctionArgumentsException()
        }

        val argumentsNumber = function.argumentNames.size
        for (i in 0 until argumentsNumber) {
            functionContext.addVariable(
                    function.argumentNames[i],
                    Variable(argumentExpressions[i].evaluate(context))
            )
        }

        val result = function.body.execute(functionContext)
        return if (result.unwind) result.value else defaultReturnValue
    }
}

data class Literal(private val value: Int) : Expression() {
    override fun evaluate(context: FixedContext): Int {
        return value
    }
}

data class BinaryExpression(
    private val operationType: BinaryOperationType,
    private val leftOperand: Expression,
    private val rightOperand: Expression
) : Expression() {
    override fun evaluate(context: FixedContext): Int {
        val leftValue = leftOperand.evaluate(context)
        val rightValue = rightOperand.evaluate(context)

        return when (operationType) {
            MULT -> leftValue * rightValue
            DIV -> leftValue / rightValue
            REM -> leftValue % rightValue
            PLUS -> leftValue + rightValue
            MINUS -> leftValue - rightValue
            LESS -> if (leftValue < rightValue) 1 else 0
            GRT -> if (leftValue > rightValue) 1 else 0
            LESS_OR_EQ -> if (leftValue <= rightValue) 1 else 0
            GRT_OR_EQ -> if (leftValue >= rightValue) 1 else 0
            EQ -> if (leftValue == rightValue) 1 else 0
            NEQ -> if (leftValue != rightValue) 1 else 0
            AND -> if (leftValue != 0 && rightValue != 0) 1 else 0
            OR -> if (leftValue != 0 || rightValue != 0) 1 else 0
        }
    }
}
