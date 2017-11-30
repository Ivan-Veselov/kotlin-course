package ru.spbau.mit.ast

import com.google.common.collect.ImmutableList
import ru.spbau.mit.*
import ru.spbau.mit.Context.FixedContext

import ru.spbau.mit.parser.FunParser

class AstFile(val body: AstBlock) {
    companion object {
        fun buildFromRuleContext(rule: FunParser.FileContext): AstFile {
            return AstFile(AstBlock.buildFromRuleContext(rule.block()))
        }
    }
}

abstract class ExecutableAstNode {
    abstract fun execute(context: Context): ExecutionResult
}

class AstBlock(private val statements: ImmutableList<AstStatement>) : ExecutableAstNode() {
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
        fun buildFromRuleContext(rule: FunParser.BlockContext): AstBlock {
            return AstBlock(ImmutableList.copyOf(
                rule.statements.map { AstStatement.buildFromRuleContext(it) }
            ))
        }
    }
}

abstract class AstStatement : ExecutableAstNode() {
    companion object {
        fun buildFromRuleContext(rule: FunParser.StatementContext) : AstStatement {
            return rule.accept(StatementContextVisitor)
        }
    }
}

class AstFunctionDefinition(
    private val name: String,
    private val parameterNames: ImmutableList<String>,
    private val body: AstBlock
) : AstStatement() {
    override fun execute(context: Context): ExecutionResult {
        if (name == BuiltinsHandler.printlnName) {
            throw PrintlnRedefinitionException()
        }

        context.addFunction(name, FunFunction(body, parameterNames, context.fixed()))
        return ExecutionResult(false)
    }
}

class AstVariableDefinition(
    private val name: String,
    private val initializingExpression: AstExpression?
) : AstStatement() {
    override fun execute(context: Context): ExecutionResult {
        val initialValue = initializingExpression?.evaluate(context.fixed()) ?: 0
        context.addVariable(name, Variable(initialValue))

        return ExecutionResult(false)
    }
}

abstract class AstExpression : AstStatement() {
    abstract fun evaluate(context: FixedContext): Int

    override fun execute(context: Context): ExecutionResult {
        evaluate(context.fixed())
        return ExecutionResult(false)
    }

    companion object {
        fun buildFromRuleContext(rule: FunParser.ExpressionContext) : AstExpression {
            return rule.accept(ExpressionContextVisitor)
        }
    }
}

class AstWhile(
    private val condition: AstExpression,
    private val body: AstBlock
) : AstStatement() {
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

class AstIf(
    private val condition: AstExpression,
    private val thenBody: AstBlock,
    private val elseBody: AstBlock?
) : AstStatement() {
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

class AstAssignment(
    private val identifier: String,
    private val expression: AstExpression
) : AstStatement() {
    override fun execute(context: Context): ExecutionResult {
        val variable = context.getVariable(identifier)
        variable.data = expression.evaluate(context.fixed())

        return ExecutionResult(false)
    }
}

class AstReturn(private val expression: AstExpression) : AstStatement() {
    override fun execute(context: Context): ExecutionResult {
        return ExecutionResult(true, expression.evaluate(context.fixed()))
    }
}

class AstVariableAccess(private val identifier: String) : AstExpression() {
    override fun evaluate(context: FixedContext): Int {
        return context.getVariable(identifier).data
    }
}

class AstFunctionCall(
    private val identifier: String,
    private val argumentExpressions: ImmutableList<AstExpression>
) : AstExpression() {
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
            throw WrongNumberOfFunctionArgumentsException(identifier)
        }

        val argumentsNumber = function.argumentNames.size
        repeat(argumentsNumber) {
            functionContext.addVariable(
                    function.argumentNames[it],
                    Variable(argumentExpressions[it].evaluate(context))
            )
        }

        val result = function.body.execute(functionContext)
        return if (result.unwind) result.value else defaultReturnValue
    }
}

class AstLiteral(private val value: Int) : AstExpression() {
    override fun evaluate(context: FixedContext): Int {
        return value
    }
}

class AstBinaryExpression(
        private val operationType: BinaryOperationType,
        private val leftOperand: AstExpression,
        private val rightOperand: AstExpression
) : AstExpression() {
    override fun evaluate(context: FixedContext): Int {
        val leftValue = leftOperand.evaluate(context)
        val rightValue = rightOperand.evaluate(context)

        return operationType.evaluate(leftValue, rightValue)
    }
}
