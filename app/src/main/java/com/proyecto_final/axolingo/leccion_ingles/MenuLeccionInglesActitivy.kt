package com.proyecto_final.axolingo.leccion_ingles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.leccion_ingles.readingActivity.ReadingActivity
import com.proyecto_final.axolingo.selector_palabras.InterfazSelector
import com.proyecto_final.axolingo.configuraciones.ConfiguracionesUsuarioActivity
import com.proyecto_final.axolingo.art.dialogs.JokeDialog
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity

// Actividad para mostrar el menú principal de las lecciones de inglés
class MenuLeccionInglesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_leccion_ingles)

        // Botón para iniciar la actividad de Spelling Bee
        val spellingBeeButton: Button = findViewById(R.id.btnSpellingBee)
        spellingBeeButton.setOnClickListener {
            val intent = Intent(this, SpellingBeeActivity::class.java)
            startActivity(intent)
        }

        // Botón para iniciar la actividad de lectura
        val btnReading: Button = findViewById(R.id.btnReading)
        btnReading.setOnClickListener {
            val intent = Intent(this, ReadingActivity::class.java)
            startActivity(intent)
        }

        // Botón para iniciar la actividad de vocabulario
        val btnVocab: Button = findViewById(R.id.btnVocabulary)
        btnVocab.setOnClickListener {
            val intent = Intent(this, com.proyecto_final.axolingo.leccion_ingles.vocabActividad.VocabActivity::class.java)
            startActivity(intent)
        }

        // Botón para iniciar la actividad de traducción
        val btnTranslator: Button = findViewById(R.id.btnTranslator)
        btnTranslator.setOnClickListener {
            val intent = Intent(this, InterfazSelector::class.java)
            startActivity(intent)
        }

        // --- LÓGICA DEL PIE DE PÁGINA ---
        // Botón para volver al menú principal
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

        // Botón para mostrar un chiste
        val btnChiste: ImageButton = findViewById(R.id.btn_chiste)
        btnChiste.setOnClickListener {
            JokeDialog(this).show()
        }
    }
}
