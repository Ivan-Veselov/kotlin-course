package ru.spbau.mit

import com.google.common.collect.ImmutableList
import ru.spbau.mit.ast.AstBlock

data class Function(
        val body: AstBlock,
        val argumentNames: ImmutableList<String>,
        val initialContext: Context.FixedContext
)
