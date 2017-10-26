package ru.spbau.mit

fun solve(n: Int, universities: List<Int>, roads: List<Pair<Int, Int>>): Int {
    return 0
}

class InvalidInputFormatException : Exception {
    constructor()

    constructor(reason: Exception) : super(reason)
}

fun main(args: Array<String>) {
    fun readLineAsIntList(): List<Int> {
        val line: String = readLine() ?: throw InvalidInputFormatException()

        try {
            return line.split(' ').map(String::toInt)
        } catch(exception: NumberFormatException) {
            throw InvalidInputFormatException(exception)
        }
    }

    fun readLineAsIntPair(): Pair<Int, Int> {
        val ints: List<Int> = readLineAsIntList()

        if (ints.size != 2) {
            throw InvalidInputFormatException()
        }

        val (i1, i2) = ints
        return Pair(i1, i2)
    }

    val (n, _) = readLineAsIntPair()
    val universities: List<Int> = readLineAsIntList()
    val roads: List<Pair<Int, Int>> = List(n - 1, { readLineAsIntPair() })

    solve(n, universities, roads)
}
