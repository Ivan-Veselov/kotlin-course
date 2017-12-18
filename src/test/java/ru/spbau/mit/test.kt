package ru.spbau.mit
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import ru.spbau.mit.receivers.Root
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class Test {
    @Test
    fun testDocumentClass() {
        val expected: String =
            """
            |\documentclass{beamer}
            |
            """.trimMargin()

        test(expected) {
            documentclass("beamer")
        }
    }

    @Test
    fun testUsePackage() {
        val expected: String =
                """
            |\usepackage[russian,english]{babel}
            |
            """.trimMargin()

        test(expected) {
            usepackage("babel", "russian", "english")
        }
    }

    @Test
    fun testDocument() {
        val expected: String =
            """
            |\begin{document}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document { }
        }
    }

    @Test
    fun testFrame() {
        val expected: String =
            """
            |\begin{document}
                |\begin{frame}
                    |\frametitle{frametitle}
                    |Text
                |\end{frame}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document {
                frame("frametitle") {
                    + "Text"
                }
            }
        }
    }

    @Test
    fun testEnumerate() {
        val expected: String =
            """
            |\begin{document}
                |\begin{frame}
                    |\frametitle{title}
                    |\begin{enumerate}
                        |\item
                        |text 1
                        |\item
                        |text 2
                    |\end{enumerate}
                |\end{frame}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document {
                frame("title") {
                    enumerate {
                        item {
                            + "text 1"
                        }

                        item {
                            + "text 2"
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testItemize() {
        val expected: String =
            """
            |\begin{document}
                |\begin{frame}
                    |\frametitle{title}
                    |\begin{itemize}
                        |\item
                        |text 1
                        |\item
                        |text 2
                    |\end{itemize}
                |\end{frame}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document {
                frame("title") {
                    itemize {
                        item {
                            + "text 1"
                        }

                        item {
                            + "text 2"
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testMath() {
        val expected: String =
            """
            |\begin{document}
                |\begin{frame}
                    |\frametitle{alpha letter}
                    |\begin{math}
                        |\alpha
                    |\end{math}
                |\end{frame}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document {
                frame("alpha letter") {
                    math {
                        + """\alpha"""
                    }
                }
            }
        }
    }

    @Test
    fun testAlign() {
        val expected: String =
            """
            |\begin{document}
                |\begin{frame}
                    |\frametitle{alignment test}
                    |\begin{align}
                        |\alpha
                    |\end{align}
                |\end{frame}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document {
                frame("alignment test") {
                    align {
                        + """\alpha"""
                    }
                }
            }
        }
    }

    @Test
    fun testCustomCommand() {
        val expected: String =
            """
            |\begin{document}
                |\begin{frame}
                    |\frametitle{custom}
                    |\begin[arg1=val1,arg2=val2]{name}
                        |Text
                    |\end{name}
                |\end{frame}
            |\end{document}
            |
            """.trimMargin()

        test(expected) {
            document {
                frame("custom") {
                    custom("name", "arg1" to "val1", "arg2" to "val2") {
                        + "Text"
                    }
                }
            }
        }
    }

    private fun test(expected: String, body: Root.() -> Unit) {
        val byteArray = ByteArrayOutputStream()
        PrintStream(byteArray).use {
            val root = Root(it)

            root.body()
            it.flush()

            assertThat(byteArray.toString(), equalTo(expected))
        }
    }
}
