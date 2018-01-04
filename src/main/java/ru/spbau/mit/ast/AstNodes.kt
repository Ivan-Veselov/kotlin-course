package ru.spbau.mit.ast

import ru.spbau.mit.*
import ru.spbau.mit.Context.FixedContext
import ru.spbau.mit.ast.visitors.AstExpressionsVisitor
import ru.spbau.mit.ast.visitors.AstNodesVisitor

import ru.spbau.mit.parser.FunParser

fun buildFromRuleContext(
    rule: FunParser.FileContext,
    listener: ExecutionListener?
): AstFile {
    return AstFile(buildFromRuleContext(rule.block(), listener))
}

fun buildFromRuleContext(
    rule: FunParser.BlockContext,
    listener: ExecutionListener?
): AstBlock {
    return AstBlock(
        rule.statements.map { buildFromRuleContext(it, listener) }
    )
}

fun buildFromRuleContext(
    rule: FunParser.StatementContext,
    listener: ExecutionListener?
) : AstStatement {
    return rule.accept(StatementContextVisitor(listener))
}

fun buildFromRuleContext(
    rule: FunParser.ExpressionContext,
    listener: ExecutionListener?
) : AstExpression {
    return rule.accept(ExpressionContextVisitor(listener))
}

abstract class AstNode {
    abstract fun <R> accept(visitor: AstNodesVisitor<R>) : R

    abstract suspend fun execute(context: Context): ExecutionResult
}

class AstFile(val body: AstBlock) : AstNode() {
    override fun toString(): String {
        return "AstFile(body=$body)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    suspend override fun execute(context: Context): ExecutionResult {
        return body.execute(context)
    }
}

class AstBlock(val statements: List<AstStatement>) : AstNode() {
    override fun toString(): String {
        return "AstBlock(statements=$statements)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun execute(context: Context): ExecutionResult {
        for (statement in statements) {
            val result = statement.execute(context)
            if (result.unwind) {
                return result
            }
        }

        return ExecutionResult(false)
    }
}

abstract class AstStatement(
    val line: Int,
    private val listener: ExecutionListener?
) : AstNode() {
    override suspend fun execute(context: Context): ExecutionResult {
        listener?.notifyExecutionStart(this, context.fixed())

        return executeStatement(context)
    }

    protected abstract suspend fun executeStatement(context: Context): ExecutionResult
}

class AstFunctionDefinition(
    private val name: String,
    private val parameterNames: List<String>,
    private val body: AstBlock,
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) {
    override fun toString(): String {
        return "AstFunctionDefinition(name=$name, parameterNames=$parameterNames, body=$body)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
        if (name == BuiltinsHandler.printlnName) {
            throw PrintlnRedefinitionException()
        }

        context.addFunction(name, FunFunction(body, parameterNames, context.fixed()))
        return ExecutionResult(false)
    }
}

class AstVariableDefinition(
    private val name: String,
    private val initializingExpression: AstExpression?,
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) {
    override fun toString(): String {
        return "AstVariableDefinition(name=$name, initializingExpression=$initializingExpression)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
        val initialValue = initializingExpression?.evaluate(context.fixed()) ?: 0
        context.addVariable(name, Variable(initialValue))

        return ExecutionResult(false)
    }
}

abstract class AstExpression(
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) { // todo: remove this inheritance?
    abstract suspend fun <R> accept(visitor: AstExpressionsVisitor<R>) : R

    suspend fun evaluate(context: FixedContext) : Int {
        return accept(AstExpressionsEvaluator(context))
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
        evaluate(context.fixed())
        return ExecutionResult(false)
    }
}

class AstWhile(
    private val condition: AstExpression,
    private val body: AstBlock,
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) {
    override fun toString(): String {
        return "AstWhile(condition=$condition, body=$body)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
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
    private val elseBody: AstBlock?,
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) {
    override fun toString(): String {
        return "AstIf(condition=$condition, thenBody=$thenBody, elseBody=$elseBody)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
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
    private val expression: AstExpression,
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) {
    override fun toString(): String {
        return "AstAssignment(identifier=$identifier, expression=$expression)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
        val variable = context.getVariable(identifier)
        variable.data = expression.evaluate(context.fixed())

        return ExecutionResult(false)
    }
}

class AstReturn(
    private val expression: AstExpression,
    line: Int,
    listener: ExecutionListener?
) : AstStatement(line, listener) {
    override fun toString(): String {
        return "AstReturn(expression=$expression)"
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    override suspend fun executeStatement(context: Context): ExecutionResult {
        return ExecutionResult(true, expression.evaluate(context.fixed()))
    }
}

class AstVariableAccess(
    val identifier: String,
    line: Int,
    listener: ExecutionListener?
) : AstExpression(line, listener) {
    override fun toString(): String {
        return "AstVariableAccess(identifier=$identifier)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstFunctionCall(
    val identifier: String,
    val argumentExpressions: List<AstExpression>,
    line: Int,
    listener: ExecutionListener?
) : AstExpression(line, listener) {
    override fun toString(): String {
        return "AstFunctionCall(identifier=$identifier, argumentExpressions=$argumentExpressions)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstLiteral(
    val value: Int,
    line: Int,
    listener: ExecutionListener?
) : AstExpression(line, listener) {
    override fun toString(): String {
        return "AstLiteral(value=$value)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstBinaryExpression(
    val operationType: BinaryOperationType,
    val leftOperand: AstExpression,
    val rightOperand: AstExpression,
    line: Int,
    listener: ExecutionListener?
) : AstExpression(line, listener) {
    override fun toString(): String {
        return "AstBinaryExpression(operationType=$operationType, leftOperand=$leftOperand, " +
                "rightOperand=$rightOperand)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }

    override fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}
