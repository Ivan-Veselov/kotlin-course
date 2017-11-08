package ru.spbau.mit

import com.google.common.collect.ImmutableList
import ru.spbau.mit.ast.Block

data class Function(
    val body: Block,
    val argumentNames: ImmutableList<String>,
    val initialContext: Context.FixedContext
)
