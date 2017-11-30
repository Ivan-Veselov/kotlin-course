package ru.spbau.mit.ast

import org.antlr.v4.runtime.Token

enum class BinaryOperationType {
    MULT, DIV, REM, PLUS, MINUS, LESS, GRT, LESS_OR_EQ, GRT_OR_EQ, EQ, NEQ, AND, OR;

    fun evaluate(l: Int, r: Int): Int {
        return when (this) {
            MULT -> l * r
            DIV -> l / r
            REM -> l % r
            PLUS -> l + r
            MINUS -> l - r
            LESS -> if (l < r) 1 else 0
            GRT -> if (l > r) 1 else 0
            LESS_OR_EQ -> if (l <= r) 1 else 0
            GRT_OR_EQ -> if (l >= r) 1 else 0
            EQ -> if (l == r) 1 else 0
            NEQ -> if (l != r) 1 else 0
            AND -> if (l != 0 && r != 0) 1 else 0
            OR -> if (l != 0 || r != 0) 1 else 0
        }
    }

    companion object {
        fun fromToken(token: Token): BinaryOperationType? {
            return when (token.text) {
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