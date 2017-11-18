package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class DocumentClassElement(
    dClass: String
) : StandaloneCommandElement() {
    override val name: String = "documentclass"

    override val mainArgument: String = dClass

    override val positionalArguments: ImmutableList<String> = ImmutableList.of()

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()
}
