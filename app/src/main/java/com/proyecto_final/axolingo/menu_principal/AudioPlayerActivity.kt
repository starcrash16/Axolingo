package com.proyecto_final.axolingo

import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

open class AudioPlayerActivity : AppCompatActivity() {

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        // Iniciamos la música cuando la actividad MenuPrincipal EMPIEZAAAA
        startMusic()
    }

    override fun onPause() {
        super.onPause()
        // Pausamos la música cuando la actividad MenuPrincipal acaba o se sale de la actividad
        pauseMusic()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Liberamos  memoria
        stopMusic()
    }

    private fun startMusic() {
        if (mediaPlayer == null) {
            // Creamos el MediaPlayer con el archivo que pusiste en res/raw
            mediaPlayer = MediaPlayer.create(this, R.raw.cancion_pou)
            mediaPlayer?.isLooping = true // Para que se repita indefinidamente
        }
        mediaPlayer?.start()
    }

    private fun pauseMusic() {
        mediaPlayer?.pause()
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}