package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class CustomCommandElement(
    override val name: String,
    vararg mutableNamedArguments: Pair<String, String>
) : BlockCommandElement() {
    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> =
        ImmutableList.copyOf(mutableNamedArguments)

    override val firstCommands: ImmutableList<StandaloneCommandElement> = ImmutableList.of()
}
