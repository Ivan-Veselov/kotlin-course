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

object ExpressionContextVisitor : FunVisitor<AstExpression> {
    override fun visitLiteralExpression(
        ctx: FunParser.LiteralExpressionContext?
    ): AstExpression {
        return AstLiteral(Integer.parseInt(ctx!!.LITERAL().text))
    }

    override fun visitVariableAccessExpression(
        ctx: FunParser.VariableAccessExpressionContext?
    ): AstExpression {
        return AstVariableAccess(ctx!!.IDENTIFIER().text)
    }

    override fun visitFunctionCallExpression(
        ctx: FunParser.FunctionCallExpressionContext?
    ): AstExpression {
        return AstFunctionCall(
            ctx!!.IDENTIFIER().text,
            ImmutableList.copyOf(ctx.arguments.map { AstExpression.buildFromRuleContext(it) })
        )
    }

    override fun visitExpressionInParentheses(
        ctx: FunParser.ExpressionInParenthesesContext?
    ): AstExpression {
        return ctx!!.expression().accept(this)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext?): AstExpression {
        return AstBinaryExpression(
            BinaryOperationType.fromString(
                ctx!!.operation.text
            ) ?: throw UnknownOperationLiteral(),
            AstExpression.buildFromRuleContext(ctx.leftOperand),
            AstExpression.buildFromRuleContext(ctx.rightOperand)
        )
    }

    override fun visitVariableDefinitionStatement(
        ctx: FunParser.VariableDefinitionStatementContext?
    ): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitTerminal(node: TerminalNode?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitChildren(node: RuleNode?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitFile(ctx: FunParser.FileContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitBlock(ctx: FunParser.BlockContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitFunctionDefinitionStatement(
        ctx: FunParser.FunctionDefinitionStatementContext?
    ): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitExpressionStatement(ctx: FunParser.ExpressionStatementContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitAssignmentStatement(ctx: FunParser.AssignmentStatementContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visit(tree: ParseTree?): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitErrorNode(node: ErrorNode?): AstExpression {
        throw NotAnExpressionException()
    }
}