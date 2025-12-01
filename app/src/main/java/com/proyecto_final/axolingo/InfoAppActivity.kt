package com.proyecto_final.axolingo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

// Actividad que muestra información sobre la aplicación
// Contiene un botón para regresar a la pantalla principal.

class InfoAppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info_app)

        val btnBack: Button = findViewById(R.id.btnBack)
        btnBack.setOnClickListener {
            // Navegar de regreso a la actividad principal
            // Se usan flags para evitar crear múltiples instancias de MainActivity
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }
}
