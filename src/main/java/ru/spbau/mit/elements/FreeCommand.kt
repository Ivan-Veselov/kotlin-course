package ru.spbau.mit.elements

open class FreeCommand : TextCommand() {
    fun custom(name: String, vararg arguments: String, init: CostumeCommand.() -> Unit) {
        addChildElement(CostumeCommand(name, *arguments), init)
    }

    fun itemize(init: Itemize.() -> Unit) {
        addChildElement(Itemize(), init)
    }

    fun enumerate(init: Enumerate.() -> Unit) {
        addChildElement(Enumerate(), init)
    }

    fun math(init: Math.() -> Unit) {
        addChildElement(Math(), init)
    }

    fun align(init: Align.() -> Unit) {
        addChildElement(Align(), init)
    }
}
