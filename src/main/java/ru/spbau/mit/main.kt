package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.apache.commons.io.FileUtils.readFileToString
import ru.spbau.mit.ast.AstFile
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.File
import java.nio.charset.Charset

fun buildAst(sourceCode: String): AstFile {
    val funLexer = FunLexer(CharStreams.fromString(sourceCode))
    val funParser = FunParser(BufferedTokenStream(funLexer))

    return AstFile.buildFromRuleContext(funParser.file())
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Invalid number of arguments. Should be 1")
        return
    }

    val charset: Charset? = null
    val sourceCode = readFileToString(File(args[0]), charset)

    // println(sourceCode)

    buildAst(sourceCode).body.execute(Context(BuiltinsHandler(System.out)))

    /*fun visit(ctx: ParserRuleContext, indent: Int) {
        print(" ".repeat(indent))
        println(ctx::class.simpleName)

        val children = ctx.getRuleContexts(ParserRuleContext::class.java)
        for (child in children) {
            visit(child, indent + 4)
        }
    }

    visit(funParser.file(), 0)*/
}
