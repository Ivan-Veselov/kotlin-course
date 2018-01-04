package ru.spbau.mit.ast.visitors

import ru.spbau.mit.ast.*

interface AstNodesVisitor<out R> {
    suspend fun visit(node: AstFile) : R

    suspend fun visit(node: AstBlock) : R

    suspend fun visit(node: AstFunctionDefinition) : R

    suspend fun visit(node: AstVariableDefinition) : R

    suspend fun visit(node: AstWhile) : R

    suspend fun visit(node: AstIf) : R

    suspend fun visit(node: AstAssignment) : R

    suspend fun visit(node: AstReturn) : R

    suspend fun visit(node: AstExpression) : R
}
