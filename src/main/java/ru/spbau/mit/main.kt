package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.tree.ParseTreeWalker
import ru.spbau.mit.parser.FunBaseListener
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser

fun main(args: Array<String>) {
    val funLexer = FunLexer(CharStreams.fromString("(1 + 2)"))

    ParseTreeWalker.DEFAULT.walk(object : FunBaseListener() {

    }, FunParser(BufferedTokenStream(funLexer)).file())
}
