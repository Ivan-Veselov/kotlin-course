package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class DocumentElement : BlockCommandElement() {
    override val name: String = "document"

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()

    override val firstCommands: ImmutableList<StandaloneCommandElement> = ImmutableList.of()
}
