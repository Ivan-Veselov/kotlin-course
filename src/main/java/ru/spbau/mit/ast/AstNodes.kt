package ru.spbau.mit.ast

import ru.spbau.mit.*
import ru.spbau.mit.Context.FixedContext

import ru.spbau.mit.parser.FunParser

fun buildFromRuleContext(rule: FunParser.FileContext): AstFile {
    return AstFile(buildFromRuleContext(rule.block()))
}

fun buildFromRuleContext(rule: FunParser.BlockContext): AstBlock {
    return AstBlock(
        rule.statements.map { buildFromRuleContext(it) }
    )
}

fun buildFromRuleContext(rule: FunParser.StatementContext) : AstStatement {
    return rule.accept(StatementContextVisitor)
}

fun buildFromRuleContext(rule: FunParser.ExpressionContext) : AstExpression {
    return rule.accept(ExpressionContextVisitor)
}

class AstFile(val body: AstBlock) {
    override fun toString(): String {
        return "AstFile(body=$body)"
    }
}

abstract class ExecutableAstNode {
    abstract fun execute(context: Context): ExecutionResult
}

class AstBlock(statements: List<AstStatement>) : ExecutableAstNode() {
    private val statements = statements.toList()

    override fun toString(): String {
        return "AstBlock(statements=$statements)"
    }

    override fun execute(context: Context): ExecutionResult {
        for (statement in statements) {
            val result = statement.execute(context)
            if (result.unwind) {
                return result
            }
        }

        return ExecutionResult(false)
    }
}

abstract class AstStatement : ExecutableAstNode()

class AstFunctionDefinition(
    private val name: String,
    parameterNames: List<String>,
    private val body: AstBlock
) : AstStatement() {
    private val parameterNames: List<String> = parameterNames.toList()

    override fun toString(): String {
        return "AstFunctionDefinition(name=$name, parameterNames=$parameterNames, body=$body)"
    }

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
    override fun toString(): String {
        return "AstVariableDefinition(name=$name, initializingExpression=$initializingExpression)"
    }

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
}

class AstWhile(
    private val condition: AstExpression,
    private val body: AstBlock
) : AstStatement() {
    override fun toString(): String {
        return "AstWhile(condition=$condition, body=$body)"
    }

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
    override fun toString(): String {
        return "AstIf(condition=$condition, thenBody=$thenBody, elseBody=$elseBody)"
    }

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
    override fun toString(): String {
        return "AstAssignment(identifier=$identifier, expression=$expression)"
    }

    override fun execute(context: Context): ExecutionResult {
        val variable = context.getVariable(identifier)
        variable.data = expression.evaluate(context.fixed())

        return ExecutionResult(false)
    }
}

class AstReturn(private val expression: AstExpression) : AstStatement() {
    override fun toString(): String {
        return "AstReturn(expression=$expression)"
    }

    override fun execute(context: Context): ExecutionResult {
        return ExecutionResult(true, expression.evaluate(context.fixed()))
    }
}

class AstVariableAccess(private val identifier: String) : AstExpression() {
    override fun toString(): String {
        return "AstVariableAccess(identifier=$identifier)"
    }

    override fun evaluate(context: FixedContext): Int {
        return context.getVariable(identifier).data
    }
}

class AstFunctionCall(
    private val identifier: String,
    argumentExpressions: List<AstExpression>
) : AstExpression() {
    private val argumentExpressions: List<AstExpression> = argumentExpressions.toList()

    override fun toString(): String {
        return "AstFunctionCall(identifier=$identifier, argumentExpressions=$argumentExpressions)"
    }

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
    override fun toString(): String {
        return "AstLiteral(value=$value)"
    }

    override fun evaluate(context: FixedContext): Int {
        return value
    }
}

class AstBinaryExpression(
    private val operationType: BinaryOperationType,
    private val leftOperand: AstExpression,
    private val rightOperand: AstExpression
) : AstExpression() {
    override fun toString(): String {
        return "AstBinaryExpression(operationType=$operationType, leftOperand=$leftOperand, " +
                "rightOperand=$rightOperand)"
    }

    override fun evaluate(context: FixedContext): Int {
        val leftValue = leftOperand.evaluate(context)
        val rightValue = rightOperand.evaluate(context)

        return operationType.evaluate(leftValue, rightValue)
    }
}
