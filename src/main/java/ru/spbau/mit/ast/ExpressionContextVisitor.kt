package ru.spbau.mit.ast

import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunVisitor

class NotAnExpressionException : Exception()

class UnknownOperationLiteral : Exception()

class ExpressionContextVisitor(
    private val listener: ExecutionListener?
) : FunVisitor<AstExpression> {
    override fun visitLiteralExpression(
        ctx: FunParser.LiteralExpressionContext
    ): AstExpression {
        return AstLiteral(
            Integer.parseInt(ctx.LITERAL().text),
            ctx.start.line,
            listener
        )
    }

    override fun visitVariableAccessExpression(
        ctx: FunParser.VariableAccessExpressionContext
    ): AstExpression {
        return AstVariableAccess(
            ctx.IDENTIFIER().text,
            ctx.start.line,
            listener
        )
    }

    override fun visitFunctionCallExpression(
        ctx: FunParser.FunctionCallExpressionContext
    ): AstExpression {
        return AstFunctionCall(
            ctx.IDENTIFIER().text,
            ctx.arguments.map { buildFromRuleContext(it, listener) },
            ctx.start.line,
            listener
        )
    }

    override fun visitExpressionInParentheses(
        ctx: FunParser.ExpressionInParenthesesContext
    ): AstExpression {
        return ctx.expression().accept(this)
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext): AstExpression {
        return AstBinaryExpression(
            BinaryOperationType.fromToken(
                ctx.operation
            ) ?: throw UnknownOperationLiteral(),
            buildFromRuleContext(ctx.leftOperand, listener),
            buildFromRuleContext(ctx.rightOperand, listener),
            ctx.start.line,
            listener
        )
    }

    override fun visitVariableDefinitionStatement(
        ctx: FunParser.VariableDefinitionStatementContext
    ): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitTerminal(node: TerminalNode): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitChildren(node: RuleNode): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitFile(ctx: FunParser.FileContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitBlock(ctx: FunParser.BlockContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitFunctionDefinitionStatement(
        ctx: FunParser.FunctionDefinitionStatementContext
    ): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitExpressionStatement(ctx: FunParser.ExpressionStatementContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitAssignmentStatement(ctx: FunParser.AssignmentStatementContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visit(tree: ParseTree): AstExpression {
        throw NotAnExpressionException()
    }

    override fun visitErrorNode(node: ErrorNode): AstExpression {
        throw NotAnExpressionException()
    }
}