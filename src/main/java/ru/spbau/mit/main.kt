package ru.spbau.mit

import java.util.*

interface GraphTraverser {
    fun stepInside(vertex: MarkedGraph.Vertex) {}

    fun stepOutside(vertex: MarkedGraph.Vertex, parent: MarkedGraph.Vertex?) {}
}

class MarkedGraph(edges: List<Pair<Int, Int>>, marks: List<Boolean>) {
    val vertices: List<Vertex>

    init {
        val numberOfVertices: Int = marks.size

        edges.filter { it.first < 0 || it.first >= numberOfVertices ||
                       it.second < 0 || it.second >= numberOfVertices }
             .forEach { throw IllegalArgumentException() }

        val neighboursId: Array<MutableList<Int>> = Array(numberOfVertices) { mutableListOf<Int>() }

        for (pair in edges) {
            neighboursId[pair.first].add(pair.second)

            if (pair.first != pair.second) {
                neighboursId[pair.second].add(pair.first)
            }
        }

        vertices = List(numberOfVertices) {
            VertexImpl(it, marks[it], neighboursId[it])
        }
    }

    fun arbitraryVertex(): Vertex {
        return vertices[0]
    }

    abstract inner class Vertex {
        abstract val id: Int

        abstract val isMarked: Boolean

        abstract fun neighbours(): Iterable<Vertex>

        fun graph(): MarkedGraph {
            return this@MarkedGraph
        }

        fun depthFirstSearch(traverser: GraphTraverser) {
            data class VertexState(val vertex: Vertex) {
                val iterator: Iterator<Vertex> = vertex.neighbours().iterator()
            }

            val visited: MutableList<Boolean> = MutableList(this@MarkedGraph.vertices.size) { false }
            val stack: LinkedList<VertexState> = LinkedList()

            val encounterNewVertex: (Vertex) -> Unit = {
                traverser.stepInside(it)
                visited[it.id] = true
                stack.push(VertexState(it))
            }

            encounterNewVertex(this)

            while (stack.isNotEmpty()) {
                val currentVertexState = stack.peek()
                if (!currentVertexState.iterator.hasNext()) {
                    stack.pop()
                    traverser.stepOutside(currentVertexState.vertex,
                                          stack.peek()?.vertex)

                    continue
                }

                val nextVertex = currentVertexState.iterator.next()
                if (visited[nextVertex.id]) {
                    continue
                }

                encounterNewVertex(nextVertex)
            }
        }
    }

    private inner class VertexImpl constructor(
        override val id: Int,
        override val isMarked: Boolean,
        private val neighbourIds: List<Int>
    ) : Vertex() {
        override fun neighbours(): Iterable<Vertex> {
            return object : Iterable<Vertex> {
                override fun iterator(): Iterator<Vertex> {
                    return object : Iterator<Vertex> {
                        private val iterator = neighbourIds.iterator()

                        override fun next(): Vertex {
                            return this@MarkedGraph.vertices[iterator.next()]
                        }

                        override fun hasNext(): Boolean {
                            return iterator.hasNext()
                        }
                    }
                }
            }
        }
    }
}

fun countSubtreeUniversities(root: MarkedGraph.Vertex) : List<Int> {
    val tree: MarkedGraph = root.graph()
    val subtreeUniversities: MutableList<Int> = MutableList(tree.vertices.size) { 0 }

    root.depthFirstSearch(object : GraphTraverser {
        override fun stepInside(vertex: MarkedGraph.Vertex) {
            if (vertex.isMarked) {
                subtreeUniversities[vertex.id] = 1
            }
        }

        override fun stepOutside(vertex: MarkedGraph.Vertex, parent: MarkedGraph.Vertex?) {
            if (parent != null) {
                subtreeUniversities[parent.id] += subtreeUniversities[vertex.id]
            }
        }
    })

    return subtreeUniversities
}

fun findCentralVertex(root: MarkedGraph.Vertex) : MarkedGraph.Vertex {
    val subtreeUniversities: List<Int> = countSubtreeUniversities(root)
    val universitiesNumber = subtreeUniversities[root.id]

    var centralVertex: MarkedGraph.Vertex? = null
    root.depthFirstSearch(object : GraphTraverser {
        override fun stepOutside(vertex: MarkedGraph.Vertex, parent: MarkedGraph.Vertex?) {
            val children = vertex.neighbours().filter { it != parent }

            if (
                children.none { subtreeUniversities[it.id] > universitiesNumber / 2 } &&
                subtreeUniversities[vertex.id] >= universitiesNumber / 2
            ) {
                centralVertex = vertex
            }
        }
    })

    return centralVertex!!
}

fun countLength(centralVertex: MarkedGraph.Vertex): Long {
    var length: Long = 0

    centralVertex.depthFirstSearch(object : GraphTraverser {
        var depth = -1

        override fun stepInside(vertex: MarkedGraph.Vertex) {
            depth++

            if (vertex.isMarked) {
                length += depth
            }
        }

        override fun stepOutside(vertex: MarkedGraph.Vertex, parent: MarkedGraph.Vertex?) {
            depth--
        }
    })

    return length
}

fun solve(numberOfVertices: Int, universities: List<Int>, roads: List<Pair<Int, Int>>): Long {
    val isUniversity: MutableList<Boolean> = MutableList(numberOfVertices) { false }
    for (vertexNumber in universities) {
        isUniversity[vertexNumber - 1] = true
    }

    val tree = MarkedGraph(roads.map { Pair(it.first - 1, it.second - 1) }, isUniversity)

    val root = tree.arbitraryVertex()
    val centralVertex = findCentralVertex(root)

    return countLength(centralVertex)
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

    print(solve(n, universities, roads))
}
