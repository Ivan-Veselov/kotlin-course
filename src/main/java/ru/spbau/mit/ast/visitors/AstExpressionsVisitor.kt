package ru.spbau.mit.ast.visitors

import ru.spbau.mit.ast.*

interface AstExpressionsVisitor<out R> {
    suspend fun visit(node: AstVariableAccess) : R

    suspend fun visit(node: AstFunctionCall) : R

    suspend fun visit(node: AstLiteral) : R

    suspend fun visit(node: AstBinaryExpression) : R
}