package ru.spbau.mit.receivers

import java.io.PrintStream

@DslMarker
annotation class LatexTagMarker

@LatexTagMarker
interface Receiver {
    val stream: PrintStream
}

class Root(
    override val stream: PrintStream
) : Receiver, HasDocumentClass, HasUsePackage, HasDocument

class Document(override val stream: PrintStream) : Receiver, HasFrame

class Frame(override val stream: PrintStream) : Receiver, FreeCommand

class Enumerate(override val stream: PrintStream) : Receiver, HasItem

class Itemize(override val stream: PrintStream) : Receiver, HasItem

class Item(override val stream: PrintStream) : Receiver, FreeCommand

class Math(override val stream: PrintStream) : Receiver, HasText

class Align(override val stream: PrintStream) : Receiver, HasText

class CustomCommand(override val stream: PrintStream) : Receiver, FreeCommand
