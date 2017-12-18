package ru.spbau.mit.elements

import com.google.common.collect.ImmutableList
import java.io.PrintStream
import java.util.stream.Collectors
import java.util.stream.Stream

abstract class CommonCommandElement {
    protected abstract val name: String

    protected abstract val positionalArguments: ImmutableList<String>

    protected abstract val namedArguments: ImmutableList<Pair<String, String>>

    protected fun renderArguments(stream: PrintStream) {
        if (positionalArguments.isNotEmpty() || namedArguments.isNotEmpty()) {
            val sequence: Stream<String> = Stream.concat(
                positionalArguments.stream(),
                namedArguments.map { pair -> pair.first + "=" + pair.second }.stream()
            )

            stream.print(sequence.collect(Collectors.joining(",", "[", "]")))
        }
    }
}

abstract class StandaloneCommandElement : CommonCommandElement(), StandaloneElement {
    protected abstract val mainArgument: String

    override fun render(stream: PrintStream) {
        stream.print("\\$name")
        renderArguments(stream)
        stream.println("{$mainArgument}")
    }
}

abstract class BlockCommandElement : CommonCommandElement(), BlockElement {
    protected abstract val firstCommands: ImmutableList<StandaloneCommandElement>

    override fun renderBegin(stream: PrintStream) {
        stream.print("\\begin")
        renderArguments(stream)
        stream.print("{$name}")
        stream.println()

        firstCommands.forEach {
            it.render(stream)
        }
    }

    override fun renderEnd(stream: PrintStream) {
        stream.println("\\end{$name}")
    }
}
