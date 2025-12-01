package com.proyecto_final.axolingo

import android.content.Context
import android.media.MediaPlayer

// MusicManager: objeto singleton para gestionar la música de fondo de la aplicación
// - Mantiene una instancia única de MediaPlayer
// - Provee utilidades para iniciar, pausar, detener y alternar la reproducción
// - Diseñado para usar `applicationContext` y evitar fugas relacionadas con Activities
object MusicManager {
    // MediaPlayer que reproduce la pista de fondo
    private var mediaPlayer: MediaPlayer? = null
    // Flag que indica si la música debería estar habilitada
    private var isMusicEnabled = true

    // Inicia la reproducción de música si está habilitada
    // Usa `applicationContext` para que la reproducción sobreviva a cambios de Activity
    fun startMusic(context: Context) {
        if (!isMusicEnabled) return
        if (mediaPlayer == null) {
            // Crear el MediaPlayer con el recurso de audio
            mediaPlayer = MediaPlayer.create(context.applicationContext, R.raw.cancion_pou)
            mediaPlayer?.isLooping = true
        }
        if (mediaPlayer?.isPlaying == false) {
            mediaPlayer?.start()
        }
    }

    // Pausa la música si se está reproduciendo
    fun pauseMusic() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    // Detiene y libera el MediaPlayer
    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // Alterna el estado de reproducción y retorna `true` si está reproduciendo
    fun toggleMusic(context: Context): Boolean {
        if (mediaPlayer?.isPlaying == true) {
            pauseMusic()
            isMusicEnabled = false
            return false // Pausado
        } else {
            isMusicEnabled = true
            startMusic(context)
            return true // Reproduciendo
        }
    }

    // Indica si actualmente se está reproduciendo música
    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying == true
    }
}
