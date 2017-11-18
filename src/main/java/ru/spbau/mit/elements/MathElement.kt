package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class MathElement : BlockCommandElement() {
    override val name: String = "math"

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()
}
