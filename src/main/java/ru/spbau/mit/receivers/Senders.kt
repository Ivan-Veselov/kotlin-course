package ru.spbau.mit.receivers

import ru.spbau.mit.elements.*
import java.io.PrintStream

interface HasDocument : Receiver {
    fun document(body: Document.() -> Unit) {
        render(Document(stream), DocumentElement(), body)
    }
}

interface HasDocumentClass : Receiver {
    fun documentclass(dClass: String) {
        render(stream, DocumentClassElement(dClass))
    }
}

interface HasUsePackage : Receiver {
    fun usepackage(packageName: String, vararg arguments: String) {
        render(stream, UsePackageElement(packageName, *arguments))
    }
}

interface HasFrame : Receiver {
    fun frame(body: Frame.() -> Unit) {
        render(Frame(stream), FrameElement(), body)
    }
}

interface HasEnumerate : Receiver {
    fun enumerate(body: Enumerate.() -> Unit) {
        render(Enumerate(stream), EnumerateElement(), body)
    }
}

interface HasItemize : Receiver {
    fun itemize(body: Itemize.() -> Unit) {
        render(Itemize(stream), ItemizeElement(), body)
    }
}

interface HasItem : Receiver {
    fun item(body: Item.() -> Unit) {
        render(Item(stream), ItemElement(), body)
    }
}

interface HasMath : Receiver {
    fun math(body: Math.() -> Unit) {
        render(Math(stream), MathElement(), body)
    }
}

interface HasAlign : Receiver {
    fun align(body: Align.() -> Unit) {
        render(Align(stream), AlignElement(), body)
    }
}

interface HasCustomCommand : Receiver {
    fun custom(
        name: String,
        vararg namedArguments: Pair<String, String>,
        body: CustomCommand.() -> Unit
    ) {
        render(CustomCommand(stream), CustomCommandElement(name, *namedArguments), body)
    }
}

interface HasText : Receiver {
    operator fun String.unaryPlus() {
        render(stream, Text(this))
    }
}

interface FreeCommand : HasCustomCommand, HasItemize, HasEnumerate, HasMath, HasAlign, HasText

fun LaTeX(stream: PrintStream, body: Root.() -> Unit) {
    val root = Root(stream)

    root.body()
}

private fun <T : Receiver> render(
    receiver: T,
    element: BlockElement,
    body: T.() -> Unit
) {
    element.renderBegin(receiver.stream)
    receiver.body()
    element.renderEnd(receiver.stream)
}

private fun <T : Receiver> render(
        receiver: T,
        element: ElementWithHeading,
        body: T.() -> Unit
) {
    element.renderHeading(receiver.stream)
    receiver.body()
}

private fun render(stream: PrintStream, element: StandaloneElement) {
    element.render(stream)
}
