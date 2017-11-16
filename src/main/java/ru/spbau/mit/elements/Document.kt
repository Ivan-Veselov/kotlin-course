package ru.spbau.mit.elements

class Document : OuterCommand() {
    fun documentclass(dClass: String) {
        addChildElement(DocumentClass(dClass))
    }

    fun usepackage(packageName: String, vararg arguments: String) {
        addChildElement(UsePackage(packageName, *arguments))
    }

    fun frame(init: Frame.() -> Unit) {
        addChildElement(Frame(), init)
    }
}

fun document(init: Document.() -> Unit) : Document {
    val document = Document()
    document.init()

    return document
}
