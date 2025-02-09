package com.luutuanhoang.assignment1

class ScoreManager {
    // Variables to track the score, current hold, and states
    var score = 0
    var currentHold = 0
    var hasFallen = false
    var hasReachedMaxScore = false

    companion object {
        // Constant for the maximum score
        const val MAX_SCORE = 18
    }

    // Method to handle climbing action
    fun climb() {
        // If the player has fallen or reached the last hold, do nothing
        if (hasFallen || currentHold >= 9) return

        // Increment the current hold
        currentHold++
        // Update the score based on the current hold
        score += when (currentHold) {
            in 1..3 -> 1
            in 4..6 -> 2
            in 7..9 -> 3
            else -> 0
        }

        // Check if the score has reached the maximum score
        if (score >= MAX_SCORE) {
            hasReachedMaxScore = true
        }
    }

    // Method to handle falling action
    fun fall() {
        // If the current hold is valid and the player hasn't fallen yet
        if (currentHold in 1..8 && !hasFallen) {
            // Decrease the score by 3, ensuring it doesn't go below 0
            score = maxOf(0, score - 3)
            // Mark that the player has fallen
            hasFallen = true
        }
    }

    // Method to reset the score manager
    fun reset() {
        score = 0
        currentHold = 0
        hasFallen = false
        hasReachedMaxScore = false
    }
}