package ru.spbau.mit.ast

import com.google.common.collect.ImmutableList
import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunVisitor

class NotAStatementException : Exception()

object StatementContextVisitor : FunVisitor<Statement> {
    override fun visitFunctionDefinitionStatement(
        ctx: FunParser.FunctionDefinitionStatementContext
    ): Statement {
        return FunctionDefinition(
            ctx.functionName.text,
            ImmutableList.copyOf(ctx.parameterNames.map { it.text }),
            Block.buildFromRuleContext(ctx.functionBody)
        )
    }

    override fun visitExpressionStatement(ctx: FunParser.ExpressionStatementContext): Statement {
        return Expression.buildFromRuleContext(ctx.expression())
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext): Statement {
        return Return(Expression.buildFromRuleContext(ctx.expression()))
    }

    override fun visitVariableDefinitionStatement(
        ctx: FunParser.VariableDefinitionStatementContext
    ): Statement {
        val initializingExpression =
            if (ctx.initialValueExpression != null) {
                Expression.buildFromRuleContext(ctx.initialValueExpression)
            } else {
                null
            }

        return VariableDefinition(
            ctx.variableName.text,
            initializingExpression
        )
    }

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext): Statement {
        return While(
            Expression.buildFromRuleContext(ctx.condition),
            Block.buildFromRuleContext(ctx.body)
        )
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext): Statement {
        val elseBody =
            if (ctx.elseBody != null) Block.buildFromRuleContext(ctx.elseBody)
            else null

        return If(
            Expression.buildFromRuleContext(ctx.condition),
            Block.buildFromRuleContext(ctx.thenBody),
            elseBody
        )
    }

    override fun visitAssignmentStatement(ctx: FunParser.AssignmentStatementContext): Statement {
        return Assignment(
            ctx.IDENTIFIER().text,
            Expression.buildFromRuleContext(ctx.expression())
        )
    }

    override fun visitFile(ctx: FunParser.FileContext): Statement {
        throw NotAStatementException()
    }

    override fun visitTerminal(node: TerminalNode): Statement {
        throw NotAStatementException()
    }

    override fun visitBlock(ctx: FunParser.BlockContext): Statement {
        throw NotAStatementException()
    }

    override fun visitChildren(node: RuleNode): Statement {
        throw NotAStatementException()
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext): Statement {
        throw NotAStatementException()
    }

    override fun visitVariableAccessExpression(
        ctx: FunParser.VariableAccessExpressionContext
    ): Statement {
        throw NotAStatementException()
    }

    override fun visitFunctionCallExpression(
        ctx: FunParser.FunctionCallExpressionContext
    ): Statement {
        throw NotAStatementException()
    }

    override fun visitLiteralExpression(ctx: FunParser.LiteralExpressionContext): Statement {
        throw NotAStatementException()
    }

    override fun visitErrorNode(node: ErrorNode): Statement {
        throw NotAStatementException()
    }

    override fun visit(tree: ParseTree): Statement {
        throw NotAStatementException()
    }

    override fun visitExpressionInParentheses(
        ctx: FunParser.ExpressionInParenthesesContext
    ): Statement {
        throw NotAStatementException()
    }
}
