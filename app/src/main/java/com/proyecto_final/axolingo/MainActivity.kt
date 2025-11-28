package com.proyecto_final.axolingo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.art.botons.BotonMenuPrincipal
import com.proyecto_final.axolingo.art.botons.BotonMenuPrincipalAzul
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.data.entity.User
import com.proyecto_final.axolingo.forms.IniciarSesion
import com.proyecto_final.axolingo.forms.LoginViewModel
import com.proyecto_final.axolingo.forms.Registrarse
import com.proyecto_final.axolingo.forms.RegistroViewModel
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.launch

// Asegúrate de que tu layout de bienvenida se llama 'menu_bienvenida.xml'
// y que contiene un botón con el id 'btnInfoApp'.

class MainActivity : BaseActivity() {
    private lateinit var registroViewModel: RegistroViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Establece el layout para esta Activity.
        setContentView(R.layout.menu_bienvenida)

        // 2. Encuentra el botón por su ID.
        val localButton: BotonMenuPrincipalAzul = findViewById(R.id.btnIniciarLocal)
        val infoAppButton: BotonMenuPrincipalAzul = findViewById(R.id.btnInfoApp)
        val loginButton:  BotonMenuPrincipal = findViewById(R.id.btnIniciarSesion)
        val signUpButton: BotonMenuPrincipal = findViewById(R.id.btnRegistro)

        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()
        val sessionManager = SessionManager(applicationContext)
        registroViewModel = RegistroViewModel(userDao)
        loginViewModel = LoginViewModel(userDao, sessionManager)

        // 3. Configura el listener para que reaccione al clic del usuario.
        localButton.setOnClickListener {
            val user = User(
                uid = 0,
                user = "local",
                email = "l@l.com",
                password = "local",
                sc_spell = 0.0f,
                sc_reading = 0.0f,
                sc_vocab = 0.0f,
                sc_transl = 0.0f,
                sc_board = 0.0f,
                sc_shapes = 0.0f
            )
            registroViewModel.registrarUsuario(user,
                onSuccess = {
                    runOnUiThread {
                        localLogin(user.user)
                    }
                },
                onConflict = {
                    runOnUiThread {
                        localLogin(user.user)
                    }
                })
        }

        infoAppButton.setOnClickListener {
            val intent = Intent(this, InfoAppActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }
    }

    private fun localLogin(user: String?) {
        loginViewModel.loginUsuario("local", "local",
            onSuccess = {
                runOnUiThread {
                    showSuccessDialog(user)
                }
            },
            onConflict = {
                runOnUiThread {
                    showConflictDialog()
                }
            })
    }

    private fun showSuccessDialog(nombreUsuario: String?) {
        AlertDialog.Builder(this)
            .setTitle("Inicio de sesión exitoso")
            .setMessage("Bienvenido $nombreUsuario")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, MenuPrincipalActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
    }

    private fun showConflictDialog() {
        AlertDialog.Builder(this)
            .setTitle("Fallo al iniciar sesión")
            .setMessage("Usuario/Correo y/o contraseña incorrectos o no encontrados")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}

