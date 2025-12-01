package com.proyecto_final.axolingo.leccion_mate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.pizarra.InterfazPizarra
import com.proyecto_final.axolingo.leccion_mate.actividad_canasta.ActividadCanasta
import com.proyecto_final.axolingo.configuraciones.ConfiguracionesUsuarioActivity
import com.proyecto_final.axolingo.art.dialogs.JokeDialog
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity

// Actividad que representa el menú principal de las lecciones de matemáticas
class MenuLeccionMateActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_leccion_mate)

        // Botón para abrir la interfaz de la pizarra
        val btnPizarra: Button = findViewById(R.id.btnPizarra)
        btnPizarra.setOnClickListener {
            val intent = Intent(this, InterfazPizarra::class.java)
            startActivity(intent)
        }

        // Botón para abrir la actividad "Canasta Matemática"
        val btnCanasta: Button = findViewById(R.id.btnCanasta)
        btnCanasta.setOnClickListener {
            val intent = Intent(this, ActividadCanasta::class.java)
            startActivity(intent)
        }

        // --- LÓGICA DEL FOOTER ---

        // Botón para regresar al menú principal
        val btnHome: ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        // Botón para abrir la configuración del usuario
        val btnSettings: ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, ConfiguracionesUsuarioActivity::class.java)
            startActivity(intent)
        }

        // Botón para mostrar un chiste en un diálogo
        val btnChiste: ImageButton = findViewById(R.id.btn_chiste)
        btnChiste.setOnClickListener {
            JokeDialog(this).show()
        }
    }
}
