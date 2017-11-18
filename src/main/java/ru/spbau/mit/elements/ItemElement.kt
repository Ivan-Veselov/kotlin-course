package ru.spbau.mit.elements

import java.io.PrintStream

class ItemElement : ElementWithHeading {
    override fun renderHeading(stream: PrintStream) {
        stream.println("\\item")
    }
}
