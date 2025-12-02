package com.proyecto_final.axolingo.configuraciones

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.dialogs.JokeDialog
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Actividad para gestionar las configuraciones del usuario
class ConfiguracionesUsuarioActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuraciones)

        // Referencias a las vistas
        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val btnChangePass = findViewById<Button>(R.id.btnChangePassword)

        // TextViews para mostrar las puntuaciones
        val tvScoreSpell = findViewById<TextView>(R.id.tvScoreSpell)
        val tvScoreReading = findViewById<TextView>(R.id.tvScoreReading)
        val tvScoreVocab = findViewById<TextView>(R.id.tvScoreVocab)
        val tvScoreTransl = findViewById<TextView>(R.id.tvScoreTransl)
        val tvScoreBoard = findViewById<TextView>(R.id.tvScoreBoard)
        val tvScoreShapes = findViewById<TextView>(R.id.tvScoreShapes)

        // Inicialización de la sesión y base de datos
        val sessionManager = SessionManager(applicationContext)
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()

        // Cargar datos del usuario y actualizar la interfaz
        lifecycleScope.launch {
            val username = sessionManager.loginFlow.first()
            if (username != null) {
                tvUsername.text = "Usuario: $username"

                // Ocultar el botón de cambiar contraseña si es un usuario local
                if (username == "local") {
                    btnChangePass.visibility = View.GONE
                } else {
                    btnChangePass.visibility = View.VISIBLE
                }

                // Cargar puntuaciones del usuario desde la base de datos
                val user = userDao.findUserToLogin(username)
                if (user != null) {
                    tvScoreSpell.text = user.sc_spell.toString()
                    tvScoreReading.text = user.sc_reading.toString()
                    tvScoreVocab.text = user.sc_vocab.toString()
                    tvScoreTransl.text = user.sc_transl.toString()
                    tvScoreBoard.text = user.sc_board.toString()
                    tvScoreShapes.text = user.sc_shapes.toString()
                }
            } else {
                // Manejo de error si no se encuentra el usuario
                tvUsername.text = "Usuario: Desconocido"
                btnChangePass.visibility = View.GONE
            }
        }

        // Configurar botón para cambiar contraseña
        btnChangePass.setOnClickListener {
            val intent = Intent(this, com.proyecto_final.axolingo.forms.CambiarContra::class.java)
            startActivity(intent)
        }

        // Configurar botón de navegación al menú principal
        val btnHome = findViewById<ImageButton>(R.id.btnHome)
        btnHome.setOnClickListener {
            val intent = Intent(this, MenuPrincipalActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish() // Cierra esta actividad
        }

        // Configurar botón flotante para mostrar chistes
        val fabButton = findViewById<ImageButton>(R.id.btn_chiste)
        fabButton.setOnClickListener {
            JokeDialog(this).show()
        }
    }
}