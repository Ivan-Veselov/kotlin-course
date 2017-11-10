package ru.spbau.mit.ast

enum class BinaryOperationType(val apply: (Int, Int) -> Int) {
    MULT({ x, y -> x * y }),
    DIV({ x, y -> x / y }),
    REM({ x, y -> x % y }),
    PLUS({ x, y -> x + y }),
    MINUS({ x, y -> x - y }),
    LESS({ x, y -> if (x < y) 1 else 0 }),
    GRT({ x, y -> if (x > y) 1 else 0 }),
    LESS_OR_EQ({ x, y -> if (x <= y) 1 else 0 }),
    GRT_OR_EQ({ x, y -> if (x >= y) 1 else 0 }),
    EQ({ x, y -> if (x == y) 1 else 0 }),
    NEQ({ x, y -> if (x != y) 1 else 0 }),
    AND({ x, y -> if (x != 0 && y != 0) 1 else 0 }),
    OR({ x, y -> if (x != 0 || y != 0) 1 else 0 });

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