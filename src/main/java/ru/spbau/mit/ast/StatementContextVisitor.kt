package ru.spbau.mit.ast

import org.antlr.v4.runtime.tree.ErrorNode
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.RuleNode
import org.antlr.v4.runtime.tree.TerminalNode
import ru.spbau.mit.parser.FunParser
import ru.spbau.mit.parser.FunVisitor

class NotAStatementException : Exception()

class StatementContextVisitor(
    private val listener: ExecutionListener?
) : FunVisitor<AstStatement> {
    override fun visitFunctionDefinitionStatement(
        ctx: FunParser.FunctionDefinitionStatementContext
    ): AstStatement {
        return AstFunctionDefinition(
            ctx.functionName.text,
            ctx.parameterNames.map { it.text },
            buildFromRuleContext(ctx.functionBody, listener),
            ctx.start.line,
            listener
        )
    }

    override fun visitExpressionStatement(ctx: FunParser.ExpressionStatementContext): AstStatement {
        return buildFromRuleContext(ctx.expression(), listener)
    }

    override fun visitReturnStatement(ctx: FunParser.ReturnStatementContext): AstStatement {
        return AstReturn(
            buildFromRuleContext(ctx.expression(), listener),
            ctx.start.line,
            listener
        )
    }

    override fun visitVariableDefinitionStatement(
        ctx: FunParser.VariableDefinitionStatementContext
    ): AstStatement {
        val initializingExpression =
            if (ctx.initialValueExpression != null) {
                buildFromRuleContext(ctx.initialValueExpression, listener)
            } else {
                null
            }

        return AstVariableDefinition(
            ctx.variableName.text,
            initializingExpression,
            ctx.start.line,
            listener
        )
    }

    override fun visitWhileStatement(ctx: FunParser.WhileStatementContext): AstStatement {
        return AstWhile(
            buildFromRuleContext(ctx.condition, listener),
            buildFromRuleContext(ctx.body, listener),
            ctx.start.line,
            listener
        )
    }

    override fun visitIfStatement(ctx: FunParser.IfStatementContext): AstStatement {
        val elseBody =
            if (ctx.elseBody != null) buildFromRuleContext(ctx.elseBody, listener)
            else null

        return AstIf(
            buildFromRuleContext(ctx.condition, listener),
            buildFromRuleContext(ctx.thenBody, listener),
            elseBody,
            ctx.start.line,
            listener
        )
    }

    override fun visitAssignmentStatement(ctx: FunParser.AssignmentStatementContext): AstStatement {
        return AstAssignment(
            ctx.IDENTIFIER().text,
            buildFromRuleContext(ctx.expression(), listener),
            ctx.start.line,
            listener
        )
    }

    override fun visitFile(ctx: FunParser.FileContext): AstStatement {
        throw NotAStatementException()
    }

    override fun visitTerminal(node: TerminalNode): AstStatement {
        throw NotAStatementException()
    }

    override fun visitBlock(ctx: FunParser.BlockContext): AstStatement {
        throw NotAStatementException()
    }

    override fun visitChildren(node: RuleNode): AstStatement {
        throw NotAStatementException()
    }

    override fun visitBinaryExpression(ctx: FunParser.BinaryExpressionContext): AstStatement {
        throw NotAStatementException()
    }

    override fun visitVariableAccessExpression(
        ctx: FunParser.VariableAccessExpressionContext
    ): AstStatement {
        throw NotAStatementException()
    }

    override fun visitFunctionCallExpression(
        ctx: FunParser.FunctionCallExpressionContext
    ): AstStatement {
        throw NotAStatementException()
    }

    override fun visitLiteralExpression(ctx: FunParser.LiteralExpressionContext): AstStatement {
        throw NotAStatementException()
    }

    override fun visitErrorNode(node: ErrorNode): AstStatement {
        throw NotAStatementException()
    }

    override fun visit(tree: ParseTree): AstStatement {
        throw NotAStatementException()
    }

    override fun visitExpressionInParentheses(
        ctx: FunParser.ExpressionInParenthesesContext
    ): AstStatement {
        throw NotAStatementException()
    }
}
