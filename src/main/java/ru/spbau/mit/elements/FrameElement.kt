package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class FrameElement : BlockCommandElement() {
    override val name: String = "frame"

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()
}
