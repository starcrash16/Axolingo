package com.proyecto_final.axolingo.configuraciones

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.dialogs.JokeDialog
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity

class ConfiguracionesUsuarioActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuraciones)

        // Configurar botón de cambiar contraseña
        val btnChangePass = findViewById<Button>(R.id.btnChangePassword)
        btnChangePass.setOnClickListener {
            Toast.makeText(this, "Funcionalidad de cambiar contraseña...", Toast.LENGTH_SHORT).show()
        }

        // Configurar navegación del Footer: Botón Home
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            // Regresar al Menu Principal
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            // Flags para limpiar el stack si es necesario, o simplemente iniciar
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Opcional: Cierra esta actividad para no acumular pantallas
        }

        // El botón Settings no hace nada porque ya estamos en Settings

        // Configurar botón FAB (Chistes)
        val fabButton = findViewById<ImageButton>(R.id.btn_chiste)
        fabButton.setOnClickListener {
            JokeDialog(this).show()
        }
    }
}