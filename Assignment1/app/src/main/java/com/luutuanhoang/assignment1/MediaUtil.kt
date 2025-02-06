package com.luutuanhoang.assignment1

import android.media.MediaPlayer

object MediaPlayerUtil {
    fun playSound(mediaPlayer: MediaPlayer) {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
            mediaPlayer.prepare()
        }
        mediaPlayer.start()
    }
}