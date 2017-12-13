package ru.spbau.mit

import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintStream

class DebuggerTest : TestClass() {
    @Test
    fun testDebugger() {
        val file = getFile("/gcd.fun")

        val commands = buildString {
            appendln("load ${file.path}")
            appendln("breakpoint 12")
            appendln("condition 6 b==3")
            appendln("list")
            appendln("run")
            appendln("evaluate a*b")
            appendln("continue")
            appendln("evaluate a+b")
            appendln("continue")
        }

        ByteOutputStream().use { outStream ->
            ByteOutputStream().use { errStream ->
                App(
                    BufferedReader(InputStreamReader(commands.byteInputStream())),
                    PrintStream(outStream),
                    PrintStream(errStream)
                ).run()

                outStream.close()

                assertThat(
                    outStream.toString(),
                    `is`(equalTo(
                        """|12: unconditional breakpoint
                        |6: conditional breakpoint
                        |104
                        |8 13
                        |13 8
                        |8 5
                        |5 3
                        |8
                        |3 2
                        |2 1
                        |1 0
                        |1
                        |""".trimMargin()
                    ))
                )
            }
        }
    }
}
