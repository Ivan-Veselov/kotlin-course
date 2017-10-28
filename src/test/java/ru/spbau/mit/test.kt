package ru.spbau.mit

import org.junit.Test

import org.junit.Assert.*

class TestSource {
    @Test
    fun test1() {
        val ans: Long = solve(7,
                listOf(1, 5, 6, 2),
                listOf(
                        Pair(1, 3),
                        Pair(3, 2),
                        Pair(4, 5),
                        Pair(3, 7),
                        Pair(4, 3),
                        Pair(4, 6)))

        assertEquals(6, ans)
    }

    @Test
    fun test2() {
        val ans: Long = solve(9,
                listOf(3, 2, 1, 6, 5, 9),
                listOf(
                        Pair(8, 9),
                        Pair(3, 2),
                        Pair(2, 7),
                        Pair(3, 4),
                        Pair(7, 6),
                        Pair(4, 5),
                        Pair(2, 1),
                        Pair(2, 8)))

        assertEquals(9, ans)
    }
}