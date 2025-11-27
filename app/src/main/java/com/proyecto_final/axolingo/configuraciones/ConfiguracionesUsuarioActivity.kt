package com.proyecto_final.axolingo.configuraciones

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.dialogs.JokeDialog
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ConfiguracionesUsuarioActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_configuraciones)

        val tvUsername = findViewById<TextView>(R.id.tvUsername)
        val btnChangePass = findViewById<Button>(R.id.btnChangePassword)
        
        // Score TextViews
        val tvScoreSpell = findViewById<TextView>(R.id.tvScoreSpell)
        val tvScoreReading = findViewById<TextView>(R.id.tvScoreReading)
        val tvScoreVocab = findViewById<TextView>(R.id.tvScoreVocab)
        val tvScoreTransl = findViewById<TextView>(R.id.tvScoreTransl)
        val tvScoreBoard = findViewById<TextView>(R.id.tvScoreBoard)
        val tvScoreShapes = findViewById<TextView>(R.id.tvScoreShapes)

        val sessionManager = SessionManager(applicationContext)
        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()

        lifecycleScope.launch {
            val username = sessionManager.loginFlow.first()
            if (username != null) {
                tvUsername.text = "Usuario: $username"
                
                if (username == "local") {
                    btnChangePass.visibility = View.GONE
                } else {
                    btnChangePass.visibility = View.VISIBLE
                }

                val user = userDao.findUserToLogin(username) // Or findUserByName if strictly name
                if (user != null) {
                    tvScoreSpell.text = user.sc_spell.toString()
                    tvScoreReading.text = user.sc_reading.toString()
                    tvScoreVocab.text = user.sc_vocab.toString()
                    tvScoreTransl.text = user.sc_transl.toString()
                    tvScoreBoard.text = user.sc_board.toString()
                    tvScoreShapes.text = user.sc_shapes.toString()
                }
            } else {
                // Should not happen if we are in this activity, but handle gracefully
                tvUsername.text = "Usuario: Desconocido"
                btnChangePass.visibility = View.GONE
            }
        }

        // Configurar botón de cambiar contraseña
        btnChangePass.setOnClickListener {
            // Navigate to Change Password Activity if it exists, or show toast
             val intent = Intent(this, com.proyecto_final.axolingo.forms.CambiarContra::class.java)
             startActivity(intent)
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