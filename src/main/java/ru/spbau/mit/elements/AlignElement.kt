package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class AlignElement : BlockCommandElement() {
    override val name = "align"

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()

    override val firstCommands: ImmutableList<StandaloneCommandElement> = ImmutableList.of()
}
