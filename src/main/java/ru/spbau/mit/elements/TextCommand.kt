package ru.spbau.mit.elements

open class TextCommand : OuterCommand() {
    operator fun String.unaryPlus() {
        addChildElement(TextElement(this))
    }
}
