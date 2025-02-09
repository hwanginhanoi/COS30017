package com.luutuanhoang.assignment1

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ScoreManagerTest {

    private lateinit var scoreManager: ScoreManager

    @Before
    fun setUp() {
        // Initialize ScoreManager before each test
        scoreManager = ScoreManager()
    }

    @Test
    fun testClimb() {
        // Test climbing increments the score correctly
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

        scoreManager.climb()
        assertEquals(9, scoreManager.score)

        scoreManager.climb()
        assertEquals(12, scoreManager.score)

        scoreManager.climb()
        assertEquals(15, scoreManager.score)

        scoreManager.climb()
        assertEquals(18, scoreManager.score)

        // Test climbing after reaching max score
        scoreManager.climb()
        assertEquals(18, scoreManager.score)
    }

    @Test
    fun testFall() {
        // Test falling decreases the score correctly
        scoreManager.score = 5
        scoreManager.fall()
        assertEquals(2, scoreManager.score)

        scoreManager.score = 1
        scoreManager.fall()
        assertEquals(0, scoreManager.score)

        // Test falling when currentHold is not in 1..8
        scoreManager.score = 9
        scoreManager.fall()
        assertEquals(9, scoreManager.score)

        // Test falling when hasFallen is true
        scoreManager.score = 14
        scoreManager.fall()
        scoreManager.fall()
        assertEquals(11, scoreManager.score)
    }

    @Test
    fun testReset() {
        // Test resetting the score manager
        scoreManager.score = 10
        scoreManager.reset()
        assertEquals(0, scoreManager.score)
        assertEquals(0, scoreManager.currentHold)
        assertEquals(false, scoreManager.hasFallen)
        assertEquals(false, scoreManager.hasReachedMaxScore)
    }

    @Test
    fun testMaxScore() {
        // Test reaching the maximum score
        for (i in 1..9) {
            scoreManager.climb()
        }
        assertEquals(18, scoreManager.score)
        assertEquals(true, scoreManager.hasReachedMaxScore)
    }

    @Test
    fun testClimbAfterFall() {
        // Test climbing after falling
        scoreManager.climb()
        scoreManager.fall()
        scoreManager.climb()
        assertEquals(1, scoreManager.score)
    }
}