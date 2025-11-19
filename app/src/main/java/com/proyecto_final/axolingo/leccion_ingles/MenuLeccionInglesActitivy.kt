package com.proyecto_final.axolingo.leccion_ingles

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.leccion_ingles.readingActivity.ReadingActivity
import com.proyecto_final.axolingo.selector_palabras.InterfazSelector

class MenuLeccionInglesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_leccion_ingles)

        val spellingBeeButton: Button = findViewById(R.id.btnSpellingBee)

        spellingBeeButton.setOnClickListener {
            val intent = Intent(this, SpellingBeeActivity::class.java)
            startActivity(intent)
        }

        val btnReading: Button = findViewById(R.id.btnReading)

        btnReading.setOnClickListener {
            val intent = Intent(this, ReadingActivity::class.java)
            startActivity(intent)
        }

        val btnVocab: Button = findViewById(R.id.btnVocabulary)

        btnVocab.setOnClickListener {
            val intent = Intent(this, VocabularyActivity::class.java)
            startActivity(intent)
        }

        val btnTranslator: Button = findViewById(R.id.btnTranslator)

        btnTranslator.setOnClickListener {
            val intent = Intent(this, InterfazSelector::class.java)
            startActivity(intent)
        }
    }
}
