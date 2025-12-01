package com.proyecto_final.axolingo.menu_vocabulario

import android.os.Bundle
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R

// Clase que representa el menú de vocabulario
class MenuVocabularyActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Establece el diseño de la actividad
        setContentView(R.layout.menu_vocabulary)
    }
}
