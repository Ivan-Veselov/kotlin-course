package ru.spbau.mit

class Graph(edges: List<Pair<Int, Int>>) {
    val vertices: List<Vertex>

    init {
        edges.filter { it.first < 0 || it.second < 0 }
             .forEach { throw IllegalArgumentException() }

        val numberOfVertices: Int = edges.flatMap { it.toList() }.max()?.inc() ?: 0
        val neighboursId: Array<MutableList<Int>> = Array(numberOfVertices) { mutableListOf<Int>() }

        for (pair in edges) {
            neighboursId[pair.first].add(pair.second)

            if (pair.first != pair.second) {
                neighboursId[pair.second].add(pair.first)
            }
        }

        vertices = List(numberOfVertices) {
            VertexImpl(neighboursId[it])
        }
    }

    interface Vertex {
        val neighbours: List<Vertex>
    }

    private inner class VertexImpl constructor(neighbourIds: List<Int>) : Vertex {
        override val neighbours: List<Vertex> by lazy {
            neighbourIds.map { vertices[it] }
        }
    }
}

fun solve(universities: List<Int>, roads: List<Pair<Int, Int>>): Int {
    val tree = Graph(roads.map { Pair(it.first - 1, it.second - 1) })

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
    val roads: List<Pair<Int, Int>> = List(n - 1) { readLineAsIntPair() }

    solve(universities, roads)
}
