package ru.spbau.mit.ast

interface AstNodesVisitor {
    fun visit(node: AstFile)

    fun visit(node: AstBlock)

    fun visit(node: AstFunctionDefinition)

    fun visit(node: AstVariableDefinition)

    fun visit(node: AstWhile)

    fun visit(node: AstIf)

    fun visit(node: AstAssignment)

    fun visit(node: AstReturn)

    fun visit(node: AstVariableAccess)

    fun visit(node: AstFunctionCall)

    fun visit(node: AstLiteral)

    fun visit(node: AstBinaryExpression)
}
