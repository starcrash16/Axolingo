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

        // --- FOOTER LOGIC ---
        val btnHome: ImageButton = findViewById(R.id.btnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }

        val btnSettings: ImageButton = findViewById(R.id.btnSettings)
        btnSettings.setOnClickListener {
            val intent = Intent(this, ConfiguracionesUsuarioActivity::class.java)
            startActivity(intent)
        }

        val btnChiste: ImageButton = findViewById(R.id.btn_chiste)
        btnChiste.setOnClickListener {
            JokeDialog(this).show()
        }
    }
}
