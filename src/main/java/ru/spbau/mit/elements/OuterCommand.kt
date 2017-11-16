package ru.spbau.mit.elements

abstract class OuterCommand : Command() {
    private val children: MutableList<Element> = mutableListOf()

    protected fun addChildElement(element: Element) {
        children.add(element)
    }

    protected fun <T : Element> addChildElement(element: T, init: T.() -> Unit) {
        element.init()
        children.add(element)
    }
}
