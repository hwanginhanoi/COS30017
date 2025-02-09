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

    // Declare variables for score management, UI elements, and media player
    private lateinit var scoreManager: ScoreManager
    private lateinit var scoreTextView: TextView
    private var hasShownPopup = false
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize score manager and UI elements
        scoreManager = ScoreManager()
        scoreTextView = findViewById(R.id.scoreTextView)
        val climbButton: Button = findViewById(R.id.climbButton)
        val fallButton: Button = findViewById(R.id.fallButton)
        val resetButton: Button = findViewById(R.id.resetButton)

        // Initialize media player with sound file
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
            hasShownPopup = savedInstanceState.getBoolean("hasShownPopup", false)
            updateScore()
        }
    }

    // Method to handle climbing action
    private fun climb() {
        if (scoreManager.score < MAX_SCORE) {
            scoreManager.climb()
            updateScore()
            Log.d("MainActivity", "Climb button clicked, score: ${scoreManager.score}")
            checkMaxScore()
        }
    }

    // Method to handle falling action
    private fun fall() {
        scoreManager.fall()
        updateScore()
        Log.d("MainActivity", "Fall button clicked, score: ${scoreManager.score}")
    }

    // Method to handle reset action
    private fun reset() {
        scoreManager.reset()
        hasShownPopup = false
        updateScore()
        Log.d("MainActivity", "Reset button clicked, score: ${scoreManager.score}")
    }

    // Method to update the score display
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

    // Method to check if the maximum score is reached
    private fun checkMaxScore() {
        if (scoreManager.score == MAX_SCORE && !scoreManager.hasReachedMaxScore && !hasShownPopup) {
            scoreManager.hasReachedMaxScore = true
            hasShownPopup = true
            showMaxScorePopup()
        }
    }

    // Method to show a popup when the maximum score is reached
    private fun showMaxScorePopup() {
        val playerNameEditText: EditText = findViewById(R.id.playerNameEditText)
        var playerName = playerNameEditText.text.toString()
        if (playerName.isEmpty()) {
            playerName = "You"
        }
        AlertDialog.Builder(this)
            .setTitle("Congratulations!")
            .setMessage("$playerName has reached the maximum score!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // Save state before the activity is destroyed
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", scoreManager.score)
        outState.putBoolean("hasShownPopup", hasShownPopup)
    }

    // Release media player resources when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}