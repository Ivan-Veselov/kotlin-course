package ru.spbau.mit.ast

enum class BinaryOperationType {
    MULT, DIV, REM, PLUS, MINUS, LESS, GRT, LESS_OR_EQ, GRT_OR_EQ, EQ, NEQ, AND, OR;

    companion object {
        fun fromString(string: String): BinaryOperationType? {
            return when (string) {
                "*" -> MULT
                "/" -> DIV
                "%" -> REM
                "+" -> PLUS
                "-" -> MINUS
                "<" -> LESS
                ">" -> GRT
                "<=" -> LESS_OR_EQ
                ">=" -> GRT_OR_EQ
                "==" -> EQ
                "!=" -> NEQ
                "&&" -> AND
                "||" -> OR
                else -> null
            }
        }
    }
}