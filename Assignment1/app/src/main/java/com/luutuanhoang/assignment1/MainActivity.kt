package com.luutuanhoang.assignment1

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.luutuanhoang.assignment1.ScoreManager.Companion.MAX_SCORE

class MainActivity : AppCompatActivity() {

    // Declare variables for managing score, UI elements, and media player
    private lateinit var scoreManager: ScoreManager
    private lateinit var scoreTextView: TextView
    private lateinit var currentHoldTextView: TextView
    private var hasShownPopup = false
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize score manager and UI elements
        scoreManager = ScoreManager()
        scoreTextView = findViewById(R.id.scoreTextView)
        currentHoldTextView = findViewById(R.id.currentHoldTextView)
        val climbButton: Button = findViewById(R.id.climbButton)
        val fallButton: Button = findViewById(R.id.fallButton)
        val resetButton: Button = findViewById(R.id.resetButton)

        // Initialize media player with button click sound
        mediaPlayer = MediaPlayer.create(this, R.raw.button_click_sound)

        // Set click listeners for buttons
        climbButton.setOnClickListener {
            MediaPlayerUtil.playSound(mediaPlayer)
            climb()
        }
        fallButton.setOnClickListener {
            MediaPlayerUtil.playSound(mediaPlayer)
            fall()
        }
        resetButton.setOnClickListener {
            MediaPlayerUtil.playSound(mediaPlayer)
            reset()
        }

        // Restore state if available
        if (savedInstanceState != null) {
            scoreManager.score = savedInstanceState.getInt("score")
            scoreManager.currentHold = savedInstanceState.getInt("currentHold")
            hasShownPopup = savedInstanceState.getBoolean("hasShownPopup", false)
            updateScore()
            updateCurrentHold()
        }
    }

    // Handle climb action
    private fun climb() {
        if (scoreManager.score < MAX_SCORE) {
            scoreManager.climb()
            updateScore()
            updateCurrentHold()
            Log.d("MainActivity", "Climb button clicked, score: ${scoreManager.score}")
            checkMaxScore()
        }
    }

    // Handle fall action
    private fun fall() {
        scoreManager.fall()
        updateScore()
        updateCurrentHold()
        Log.d("MainActivity", "Fall button clicked, score: ${scoreManager.score}")
    }

    // Handle reset action
    private fun reset() {
        scoreManager.reset()
        hasShownPopup = false
        updateScore()
        updateCurrentHold()
        Log.d("MainActivity", "Reset button clicked, score: ${scoreManager.score}")
    }

    // Update score display
    private fun updateScore() {
        scoreTextView.text = getString(R.string.score, scoreManager.score)
        scoreTextView.setTextColor(
            when {
                scoreManager.score < 3 -> getColor(R.color.blue)
                scoreManager.score < 6 -> getColor(R.color.green)
                else -> getColor(R.color.red)
            }
        )
    }

    // Update current hold display
    private fun updateCurrentHold() {
        currentHoldTextView.text = getString(R.string.current_hold, scoreManager.currentHold)
    }

    // Check if the maximum score has been reached and show popup if necessary
    private fun checkMaxScore() {
        if (scoreManager.score == MAX_SCORE && scoreManager.hasReachedMaxScore && !hasShownPopup) {
            scoreManager.hasReachedMaxScore = true
            hasShownPopup = true
            showMaxScorePopup()
        }
    }

    // Show popup when maximum score is reached
    private fun showMaxScorePopup() {
        val playerNameEditText: EditText = findViewById(R.id.playerNameEditText)
        var playerName = playerNameEditText.text.toString()
        if (playerName.isEmpty()) {
            playerName = "You"
        }
        AlertDialog.Builder(this)
            .setTitle(getString(R.string.congratulations))
            .setMessage(getString(R.string.max_score_message, playerName))
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Save instance state
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", scoreManager.score)
        outState.putBoolean("hasShownPopup", hasShownPopup)
        outState.putInt("currentHold", scoreManager.currentHold)
    }

    // Release media player resources
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}