package com.luutuanhoang.assignment1

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ScoreManagerTest {

    private lateinit var scoreManager: ScoreManager

    @Before
    fun setUp() {
        scoreManager = ScoreManager()
    }

    @Test
    fun testClimb() {
        scoreManager.climb()
        assertEquals(1, scoreManager.score)

        scoreManager.climb()
        assertEquals(2, scoreManager.score)

        scoreManager.climb()
        assertEquals(3, scoreManager.score)

        scoreManager.climb()
        assertEquals(5, scoreManager.score)

        scoreManager.climb()
        assertEquals(7, scoreManager.score)
    }

    @Test
    fun testFall() {
        scoreManager.score = 5
        scoreManager.fall()
        assertEquals(2, scoreManager.score)

        scoreManager.score = 1
        scoreManager.fall()
        assertEquals(0, scoreManager.score)

        scoreManager.score = 9
        scoreManager.fall()
        assertEquals(9, scoreManager.score)

        scoreManager.score = 14
        scoreManager.fall()
        assertEquals(14, scoreManager.score)
    }

    @Test
    fun testReset() {
        scoreManager.score = 10
        scoreManager.reset()
        assertEquals(0, scoreManager.score)
    }
}