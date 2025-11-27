package com.proyecto_final.axolingo

import android.content.Context
import android.media.MediaPlayer

object MusicManager {
    private var mediaPlayer: MediaPlayer? = null
    private var isMusicEnabled = true

    fun startMusic(context: Context) {
        if (!isMusicEnabled) return
        if (mediaPlayer == null) {
            // Usamos applicationContext para que se aplique en toda la aplicación y no solo en un layout
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.cancion_pou)
            mediaPlayer?.isLooping = true
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun toggleMusic(context: Context): Boolean {
        if (mediaPlayer?.isPlaying == true) {
            pauseMusic()
            isMusicEnabled = false
            return false // Paused
        } else {
            isMusicEnabled = true
            startMusic(context)
            return true // Playing
        }
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
