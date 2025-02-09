package com.luutuanhoang.assignment1

import android.media.MediaPlayer

object MediaPlayerUtil {
    // Function to play a sound using the provided MediaPlayer instance
    fun playSound(mediaPlayer: MediaPlayer) {
        // If the media player is already playing, stop and prepare it again
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare()
        }
        // Start playing the sound
        mediaPlayer.start()
    }
}