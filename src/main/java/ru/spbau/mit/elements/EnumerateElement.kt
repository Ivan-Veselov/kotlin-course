package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class EnumerateElement : BlockCommandElement() {
    override val name: String = "enumerate"

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()
}
