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

        assert(edges.none {
            it.first < 0 || it.first >= numberOfVertices ||
            it.second < 0 || it.second >= numberOfVertices
        })

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

    fun findCentralVertex() : MarkedGraph.Vertex {
        val root = vertices[0]
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

    private fun countSubtreeUniversities(root: MarkedGraph.Vertex) : List<Int> {
        val subtreeUniversities: MutableList<Int> = MutableList(vertices.size) { 0 }

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

    abstract inner class Vertex {
        abstract val id: Int

        abstract val isMarked: Boolean

        abstract fun neighbours(): Iterable<Vertex>

        fun depthFirstSearch(traverser: GraphTraverser) {
            data class VertexState(val vertex: Vertex) {
                val iterator: Iterator<Vertex> = vertex.neighbours().iterator()
            }

            val visited: MutableList<Boolean> =
                    MutableList(this@MarkedGraph.vertices.size) { false }
            val stack: LinkedList<VertexState> = LinkedList()

            fun encounterNewVertex(vertex: Vertex) {
                traverser.stepInside(vertex)
                visited[vertex.id] = true
                stack.push(VertexState(vertex))
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
        override fun neighbours(): Iterable<Vertex> = neighbourIds.map {
            this@MarkedGraph.vertices[it]
        }
    }
}

fun summaryDistanceTo(vertex: MarkedGraph.Vertex): Long {
    var length: Long = 0

    vertex.depthFirstSearch(object : GraphTraverser {
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
    val centralVertex = tree.findCentralVertex()

    return summaryDistanceTo(centralVertex)
}

fun main(args: Array<String>) {
    fun readLineAsIntList(): List<Int> {
        val line: String = readLine() ?: throw IllegalArgumentException()

        return line.split(' ').map(String::toInt)
    }

    fun readLineAsIntPair(): Pair<Int, Int> {
        val ints: List<Int> = readLineAsIntList()

        if (ints.size != 2) {
            throw IllegalArgumentException()
        }

        return Pair(ints[0], ints[1])
    }

    val (n, _) = readLineAsIntPair()
    val universities: List<Int> = readLineAsIntList()
    val roads: List<Pair<Int, Int>> = List(n - 1) { readLineAsIntPair() }

    print(solve(n, universities, roads))
}
