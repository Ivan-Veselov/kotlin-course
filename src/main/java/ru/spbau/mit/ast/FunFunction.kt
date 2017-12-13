package ru.spbau.mit.ast

import ru.spbau.mit.Context

class FunFunction(
    val body: AstBlock,
    argumentNames: List<String>,
    val initialContext: Context.FixedContext
) {
    val argumentNames: List<String> = argumentNames.toList()
}
