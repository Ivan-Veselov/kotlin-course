package ru.spbau.mit.ast.visitors

import ru.spbau.mit.ast.*

interface AstNodesVisitor<out R> {
    fun visit(node: AstFile) : R

    fun visit(node: AstBlock) : R

    fun visit(node: AstFunctionDefinition) : R

    fun visit(node: AstVariableDefinition) : R

    fun visit(node: AstWhile) : R

    fun visit(node: AstIf) : R

    fun visit(node: AstAssignment) : R

    fun visit(node: AstReturn) : R

    fun visit(node: AstVariableAccess) : R

    fun visit(node: AstFunctionCall) : R

    fun visit(node: AstLiteral) : R

    fun visit(node: AstBinaryExpression) : R
}
