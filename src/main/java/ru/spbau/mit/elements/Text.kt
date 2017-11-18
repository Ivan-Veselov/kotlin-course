package ru.spbau.mit.elements

import java.io.PrintStream

class Text(val text: String) : StandaloneElement {
    override fun render(stream: PrintStream) {
        stream.println(text)
    }
}
