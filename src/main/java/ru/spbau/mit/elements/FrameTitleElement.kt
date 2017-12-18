package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class FrameTitleElement(
    title: String
) : StandaloneCommandElement() {
    override val name: String = "frametitle"

    override val mainArgument: String = title

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()
}
