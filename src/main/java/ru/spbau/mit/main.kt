package ru.spbau.mit

import ru.spbau.mit.receivers.LaTeX

fun main(args: Array<String>) {
    LaTeX(System.out) {
        documentclass("beamer")
        usepackage("babel", "russian")

        document {
            frame { // (frameTitle="frametitle", "arg1" to "arg2")
                itemize {
                    for (row in 1..10) {
                        item { + "$row text" }
                    }
                }
            }
        }
    }
}
