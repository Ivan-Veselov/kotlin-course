package ru.spbau.mit

import java.io.PrintStream

class BuiltinsHandler(private val stream: PrintStream) {
    fun println(values: List<Int>) {
        if (values.isNotEmpty()) {
            stream.print(values[0])
        }

        for (value in values.subList(1, values.size)) {
            stream.print(' ')
            stream.print(value)
        }

        stream.println()
    }

    companion object {
        val printlnName: String = "println"
    }
}