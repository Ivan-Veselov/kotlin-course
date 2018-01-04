package ru.spbau.mit.ast

import ru.spbau.mit.Context.FixedContext
import ru.spbau.mit.ast.visitors.AstExpressionsVisitor
import ru.spbau.mit.ast.visitors.AstNodesVisitor
import ru.spbau.mit.parser.FunParser

fun buildFromRuleContext(
    rule: FunParser.FileContext
): AstFile {
    return AstFile(buildFromRuleContext(rule.block()))
}

fun buildFromRuleContext(
    rule: FunParser.BlockContext
): AstBlock {
    return AstBlock(
        rule.statements.map { buildFromRuleContext(it) }
    )
}

fun buildFromRuleContext(
    rule: FunParser.StatementContext
) : AstStatement {
    return rule.accept(StatementContextVisitor())
}

fun buildFromRuleContext(
    rule: FunParser.ExpressionContext
) : AstExpression {
    return rule.accept(ExpressionContextVisitor())
}

abstract class AstNode {
    abstract suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R
}

class AstFile(val body: AstBlock) : AstNode() {
    override fun toString(): String {
        return "AstFile(body=$body)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstBlock(val statements: List<AstStatement>) : AstNode() {
    override fun toString(): String {
        return "AstBlock(statements=$statements)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

abstract class AstStatement(
    val line: Int
) : AstNode()

class AstFunctionDefinition(
    val name: String,
    val parameterNames: List<String>,
    val body: AstBlock,
    line: Int
) : AstStatement(line) {
    override fun toString(): String {
        return "AstFunctionDefinition(name=$name, parameterNames=$parameterNames, body=$body)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstVariableDefinition(
    val name: String,
    val initializingExpression: AstExpression?,
    line: Int
) : AstStatement(line) {
    override fun toString(): String {
        return "AstVariableDefinition(name=$name, initializingExpression=$initializingExpression)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

abstract class AstExpression(
    line: Int
) : AstStatement(line) {
    abstract suspend fun <R> accept(visitor: AstExpressionsVisitor<R>) : R

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }

    suspend fun evaluate(context: FixedContext, listener: ExecutionListener?) : Int {
        return accept(AstExpressionsEvaluator(context, listener))
    }
}

class AstWhile(
    val condition: AstExpression,
    val body: AstBlock,
    line: Int
) : AstStatement(line) {
    override fun toString(): String {
        return "AstWhile(condition=$condition, body=$body)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstIf(
    val condition: AstExpression,
    val thenBody: AstBlock,
    val elseBody: AstBlock?,
    line: Int
) : AstStatement(line) {
    override fun toString(): String {
        return "AstIf(condition=$condition, thenBody=$thenBody, elseBody=$elseBody)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstAssignment(
    val identifier: String,
    val expression: AstExpression,
    line: Int
) : AstStatement(line) {
    override fun toString(): String {
        return "AstAssignment(identifier=$identifier, expression=$expression)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstReturn(
    val expression: AstExpression,
    line: Int
) : AstStatement(line) {
    override fun toString(): String {
        return "AstReturn(expression=$expression)"
    }

    override suspend fun <R> accept(visitor: AstNodesVisitor<R>) : R {
        return visitor.visit(this)
    }
}

class AstVariableAccess(
    val identifier: String,
    line: Int
) : AstExpression(line) {
    override fun toString(): String {
        return "AstVariableAccess(identifier=$identifier)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }
}

class AstFunctionCall(
    val identifier: String,
    val argumentExpressions: List<AstExpression>,
    line: Int
) : AstExpression(line) {
    override fun toString(): String {
        return "AstFunctionCall(identifier=$identifier, argumentExpressions=$argumentExpressions)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }
}

class AstLiteral(
    val value: Int,
    line: Int
) : AstExpression(line) {
    override fun toString(): String {
        return "AstLiteral(value=$value)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }
}

class AstBinaryExpression(
    val operationType: BinaryOperationType,
    val leftOperand: AstExpression,
    val rightOperand: AstExpression,
    line: Int
) : AstExpression(line) {
    override fun toString(): String {
        return "AstBinaryExpression(operationType=$operationType, leftOperand=$leftOperand, " +
                "rightOperand=$rightOperand)"
    }

    override suspend fun <R> accept(visitor: AstExpressionsVisitor<R>): R {
        return visitor.visit(this)
    }
}
