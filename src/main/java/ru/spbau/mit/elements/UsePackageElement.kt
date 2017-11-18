package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList

class UsePackageElement(
    packageName: String,
    vararg arguments: String
) : StandaloneCommandElement() {
    override val name: String = "usepackage"

    override val mainArgument: String = packageName

    override val positionalArguments: ImmutableList<String> = ImmutableList.copyOf(arguments)

    override val namedArguments: ImmutableList<Pair<String, String>> = ImmutableList.of()
}
