package ru.spbau.mit.elements

abstract class ItemCommand : OuterCommand() {
    fun item(init: Item.() -> Unit) {
        addChildElement(Item(), init)
    }
}
