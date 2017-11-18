package ru.spbau.mit.elements

import java.io.PrintStream

interface Element

interface StandaloneElement : Element {
    fun render(stream: PrintStream)
}

interface BlockElement : Element {
    fun renderBegin(stream: PrintStream)

    fun renderEnd(stream: PrintStream)
}

interface ElementWithHeading : Element {
    fun renderHeading(stream: PrintStream)
}
