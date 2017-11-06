package ru.spbau.mit

import ru.spbau.mit.parser.FunParser

class Function(
    definitionStatement: FunParser.FunctionDefinitionStatementContext,
    val initialContext: Context.FixedContext
) {
    val argumentNames: List<String> =
            definitionStatement.parameterNames.map { it.text }

    val body: FunParser.BlockContext = definitionStatement.functionBody
}
