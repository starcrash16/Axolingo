package com.proyecto_final.axolingo.leccion_mate

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.pizarra.InterfazPizarra
import com.proyecto_final.axolingo.leccion_mate.actividad_canasta.ActividadCanasta

class MenuLeccionMateActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_leccion_mate)

        val btnPizarra: Button = findViewById(R.id.btnPizarra)
        btnPizarra.setOnClickListener {
            val intent = Intent(this, InterfazPizarra::class.java)
            startActivity(intent)
        }

        val btnCanasta: Button = findViewById(R.id.btnCanasta)
        btnCanasta.setOnClickListener {
            val intent = Intent(this, ActividadCanasta::class.java)
            startActivity(intent)
        }
    }
}
