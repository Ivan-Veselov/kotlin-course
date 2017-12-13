package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import ru.spbau.mit.ast.AstFile
import ru.spbau.mit.ast.ExecutionListener
import ru.spbau.mit.ast.buildFromRuleContext
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.BufferedReader
import java.io.InputStreamReader

class FailedToParseException : Exception()

fun buildAst(sourceCode: String, listener: ExecutionListener?): AstFile {
    val funLexer = FunLexer(CharStreams.fromString(sourceCode))
    val funParser = FunParser(BufferedTokenStream(funLexer))

    if (funParser.numberOfSyntaxErrors > 0) {
        throw FailedToParseException()
    }

    return buildFromRuleContext(funParser.file(), listener)
}

fun main(args: Array<String>) {
    App(BufferedReader(InputStreamReader(System.`in`)), System.out, System.err).run()
}
