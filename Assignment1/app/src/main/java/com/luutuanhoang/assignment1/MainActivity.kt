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

    private lateinit var scoreManager: ScoreManager
    private lateinit var scoreTextView: TextView
    private var hasShownPopup = false
    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        scoreManager = ScoreManager()
        scoreTextView = findViewById(R.id.scoreTextView)
        val climbButton: Button = findViewById(R.id.climbButton)
        val fallButton: Button = findViewById(R.id.fallButton)
        val resetButton: Button = findViewById(R.id.resetButton)

        mediaPlayer = MediaPlayer.create(this, R.raw.button_click_sound)

        mediaPlayer = MediaPlayer.create(this, R.raw.button_click_sound)

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

        if (savedInstanceState != null) {
            scoreManager.score = savedInstanceState.getInt("score")
            hasShownPopup = savedInstanceState.getBoolean("hasShownPopup", false)
            updateScore()
        }
    }

    private fun climb() {
        if (scoreManager.score < MAX_SCORE) {
            scoreManager.climb()
            updateScore()
            Log.d("MainActivity", "Climb button clicked, score: ${scoreManager.score}")
            checkMaxScore()
        }
    }

    private fun fall() {
        scoreManager.fall()
        updateScore()
        Log.d("MainActivity", "Fall button clicked, score: ${scoreManager.score}")
    }

    private fun reset() {
        scoreManager.reset()
        hasShownPopup = false
        updateScore()
        Log.d("MainActivity", "Reset button clicked, score: ${scoreManager.score}")
    }

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

    private fun checkMaxScore() {
        if (scoreManager.score == MAX_SCORE && scoreManager.hasReachedMaxScore && !hasShownPopup) {
            scoreManager.hasReachedMaxScore = true
            hasShownPopup = true
            showMaxScorePopup()
        }
    }

    private fun showMaxScorePopup() {
        val playerNameEditText: EditText = findViewById(R.id.playerNameEditText)
        var playerName = playerNameEditText.text.toString()
        if (playerName.isEmpty()) {
            playerName = "you"
        }
        AlertDialog.Builder(this)
            .setTitle("Congratulations!")
            .setMessage("$playerName has reached the maximum score!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("score", scoreManager.score)
        outState.putBoolean("hasShownPopup", hasShownPopup)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
}