package ru.spbau.mit.ast

import com.google.common.collect.ImmutableList
import ru.spbau.mit.Context

data class FunFunction(
        val body: AstBlock,
        val argumentNames: ImmutableList<String>,
        val initialContext: Context.FixedContext
)
