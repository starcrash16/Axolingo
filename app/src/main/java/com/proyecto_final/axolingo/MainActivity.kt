package com.proyecto_final.axolingo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.proyecto_final.axolingo.forms.IniciarSesion
import com.proyecto_final.axolingo.forms.Registrarse
import com.proyecto_final.axolingo.views.MenuPrincipal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.menu_bienvenida)
        /*ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }*/

        var btnRegistro: Button = findViewById(R.id.btnRegistro)
        var btnLogin: Button = findViewById(R.id.btnIniciarSesion)

        btnRegistro.setOnClickListener(evento)
        btnLogin.setOnClickListener(evento)
    }

    val evento = View.OnClickListener { view ->
        when (view.getId()) {
            R.id.btnRegistro -> {
                val intent = Intent(this, Registrarse::class.java)
                startActivity(intent)
            }
            R.id.btnIniciarSesion -> {
                val intent = Intent(this, IniciarSesion::class.java)
                startActivity(intent)
            }
        }
    }
}