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

// MainActivity: Pantalla de bienvenida / entry point de la app
// - Permite crear un usuario local de prueba
// - Redirige a pantallas de información, inicio de sesión y registro
// - Gestiona la lógica inicial de registro/login local usando ViewModels (RegistroViewModel, LoginViewModel)
class MainActivity : BaseActivity() {
    private lateinit var registroViewModel: RegistroViewModel
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Establece el layout para esta Activity.
        setContentView(R.layout.menu_bienvenida)

        // 2. Obtén referencias a botones personalizados del layout
        val localButton: BotonMenuPrincipalAzul = findViewById(R.id.btnIniciarLocal)
        val infoAppButton: BotonMenuPrincipalAzul = findViewById(R.id.btnInfoApp)
        val loginButton:  BotonMenuPrincipal = findViewById(R.id.btnIniciarSesion)
        val signUpButton: BotonMenuPrincipal = findViewById(R.id.btnRegistro)

        // 3. Inicializa DAO y ViewModels necesarios para registro/login
        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()
        val sessionManager = SessionManager(applicationContext)
        registroViewModel = RegistroViewModel(userDao)
        loginViewModel = LoginViewModel(userDao, sessionManager)

        // 4. Configura listeners de los botones
        // Botón para crear/usar un usuario local (útil para pruebas sin autenticación remota)
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
            // Intenta registrar el usuario local; si ya existe, también procede al login local
            registroViewModel.registrarUsuario(user,
                onSuccess = {
                    runOnUiThread { localLogin(user.user) }
                },
                onConflict = {
                    runOnUiThread { localLogin(user.user) }
                })
        }

        // Botón para abrir la pantalla de información de la app
        infoAppButton.setOnClickListener {
            val intent = Intent(this, InfoAppActivity::class.java)
            startActivity(intent)
        }

        // Botón para navegar a la pantalla de inicio de sesión
        loginButton.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }

        // Botón para navegar a la pantalla de registro
        signUpButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }
    }

    // Realiza login local usando las credenciales predefinidas
    private fun localLogin(user: String?) {
        loginViewModel.loginUsuario("local", "local",
            onSuccess = {
                runOnUiThread { showSuccessDialog(user) }
            },
            onConflict = {
                runOnUiThread { showConflictDialog() }
            })
    }

    // Muestra un diálogo indicando inicio de sesión exitoso y navega al menú principal
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

    // Muestra un diálogo cuando falla el inicio de sesión local
    private fun showConflictDialog() {
        AlertDialog.Builder(this)
            .setTitle("Fallo al iniciar sesión")
            .setMessage("Usuario/Correo y/o contraseña incorrectos o no encontrados")
            .setPositiveButton("Aceptar") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}

