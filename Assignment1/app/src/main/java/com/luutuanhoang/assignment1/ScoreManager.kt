package com.luutuanhoang.assignment1

class ScoreManager {
    var score = 0
    private var currentHold = 0
    private var hasFallen = false
    var hasReachedMaxScore = false
    companion object {
        const val MAX_SCORE = 18
    }

    fun climb() {
        if (hasFallen || currentHold >= 9) return

        currentHold++
        score += when (currentHold) {
            in 1..3 -> 1
            in 4..6 -> 2
            in 7..9 -> 3
            else -> 0
        }

        if (score >= MAX_SCORE) {
            hasReachedMaxScore = true
        }
    }

    fun fall() {
        if (currentHold in 1..8 && !hasFallen) {
            score = maxOf(0, score - 3)
            hasFallen = true
        }
    }

    fun reset() {
        score = 0
        currentHold = 0
        hasFallen = false
        hasReachedMaxScore = false
    }
}