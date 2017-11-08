package ru.spbau.mit.ast

import com.google.common.collect.ImmutableList
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunVisitor

class NotAnExpressionException : Exception()

class UnknownOperationLiteral : Exception()

object ExpressionContextVisitor : FunVisitor<Expression> {
    override fun visitLiteralExpression(
        ctx: FunParser.LiteralExpressionContext?
    ): Expression {
        return Literal(Integer.parseInt(ctx!!.LITERAL().text))
    }

    override fun visitVariableAccessExpression(
        ctx: FunParser.VariableAccessExpressionContext?
    ): Expression {
        return VariableAccess(ctx!!.IDENTIFIER().text)
    }

    override fun visitFunctionCallExpression(
        ctx: FunParser.FunctionCallExpressionContext?
    ): Expression {
        return FunctionCall(
            ctx!!.IDENTIFIER().text,
            ImmutableList.copyOf(ctx.arguments.map { Expression.buildFromRuleContext(it) })
        )
    }

    override fun visitExpressionInParentheses(
        ctx: FunParser.ExpressionInParenthesesContext?
    ): Expression {
        return ctx!!.expression().accept(this)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext?): Expression {
        return BinaryExpression(
            BinaryOperationType.fromString(
                ctx!!.operation.text
            ) ?: throw UnknownOperationLiteral(),
            Expression.buildFromRuleContext(ctx.leftOperand),
            Expression.buildFromRuleContext(ctx.rightOperand)
        )
    }

    override fun visitVariableDefinitionStatement(
        ctx: FunParser.VariableDefinitionStatementContext?
    ): Expression {
        throw NotAnExpressionException()
    }

    override fun visitTerminal(node: TerminalNode?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitChildren(node: RuleNode?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitFile(ctx: FunParser.FileContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitBlock(ctx: FunParser.BlockContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitFunctionDefinitionStatement(
        ctx: FunParser.FunctionDefinitionStatementContext?
    ): Expression {
        throw NotAnExpressionException()
    }

    override fun visitExpressionStatement(ctx: FunParser.ExpressionStatementContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitAssignmentStatement(ctx: FunParser.AssignmentStatementContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext?): Expression {
        throw NotAnExpressionException()
    }

    override fun visit(tree: ParseTree?): Expression {
        throw NotAnExpressionException()
    }

    override fun visitErrorNode(node: ErrorNode?): Expression {
        throw NotAnExpressionException()
    }
}