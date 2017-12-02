package ru.spbau.mit

import org.antlr.v4.runtime.BufferedTokenStream
import org.antlr.v4.runtime.CharStreams
import org.apache.commons.io.FileUtils.readFileToString
import ru.spbau.mit.ast.AstFile
import ru.spbau.mit.ast.PrintlnRedefinitionException
import ru.spbau.mit.ast.WrongNumberOfFunctionArgumentsException
import ru.spbau.mit.ast.buildFromRuleContext
import ru.spbau.mit.parser.FunLexer
import ru.spbau.mit.parser.FunParser
import java.io.File
import java.nio.charset.Charset

class FailedToParseException : Exception()

fun buildAst(sourceCode: String): AstFile {
    val funLexer = FunLexer(CharStreams.fromString(sourceCode))
    val funParser = FunParser(BufferedTokenStream(funLexer))

    if (funParser.numberOfSyntaxErrors > 0) {
        throw FailedToParseException()
    }

    return buildFromRuleContext(funParser.file())
}

fun main(args: Array<String>) {
    if (args.size != 1) {
        println("Invalid number of arguments. Should be 1")
        return
    }

    val charset: Charset? = null
    val sourceCode = readFileToString(File(args[0]), charset)

    try {
        buildAst(sourceCode).body.execute(Context(BuiltinsHandler(System.out)))
    } catch (e: FailedToParseException ) {
        // all messages were written by antlr
    } catch (e: ContextSymbolOverwritingException) {
        System.err.println("Symbol overwriting: " + e.symbol)
    } catch (e: ContextUndefinedSymbolException) {
        System.err.println("Undefined symbol: " + e.symbol)
    } catch (e: WrongNumberOfFunctionArgumentsException) {
        System.err.println("Invalid number of arguments when calling " + e.functionName)
    } catch (e: PrintlnRedefinitionException) {
        System.err.println("Attempt to redefine builtin println function")
    }
}
