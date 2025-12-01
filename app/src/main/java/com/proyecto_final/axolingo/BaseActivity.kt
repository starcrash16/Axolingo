package com.proyecto_final.axolingo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

// BaseActivity: clase base para Activities de la app
// - Oculta la barra de navegación para proporcionar una experiencia inmersiva
// - Reaplica la visibilidad inmersiva cuando la ventana recupera el foco
// Todas las Activities que extiendan esta clase heredarán este comportamiento

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Ocultar la barra de navegación para modo inmersivo
        // Usamos FLAG_IMMERSIVE_STICKY para que la UI vuelva a ocultarse automáticamente
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            // Cuando la ventana recupera el foco, reaplicamos el modo inmersivo
            // Esto evita que la barra de navegación quede visible tras diálogos u otras interacciones
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }
    }
}