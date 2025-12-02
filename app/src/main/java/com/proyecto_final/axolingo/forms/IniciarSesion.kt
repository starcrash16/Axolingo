package com.proyecto_final.axolingo.forms

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.databinding.InicioSesionBinding
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager

// Actividad para manejar el inicio de sesión
class IniciarSesion : BaseActivity(){
    private lateinit var binding: InicioSesionBinding // Enlace con el diseño XML
    private lateinit var loginViewModel: LoginViewModel // ViewModel para manejar la lógica de inicio de sesión

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del DAO y ViewModel
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        val sessionManager = SessionManager(applicationContext)
        loginViewModel = LoginViewModel(userDao, sessionManager)
        binding = InicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del botón para recuperar contraseña
        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, CambiarContra::class.java)
            startActivity(intent)
        }

        // Configuración del botón para registrarse
        binding.regButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }

        // Configuración del botón para iniciar sesión
        binding.btnLogin.setOnClickListener {
            if (isDataValid()) { // Verificar si los datos ingresados son válidos
                val userData = binding.editUser.text.toString().trim()
                val userPass = binding.editPassword.text.toString().trim()
                loginViewModel.loginUsuario(userData, userPass,
                    onSuccess = { user ->
                        runOnUiThread {
                            showSuccessDialog(user.user) // Mostrar diálogo de éxito
                        }
                    },
                    onConflict = {
                        runOnUiThread {
                            showConflictDialog() // Mostrar diálogo de error
                        }
                    })
            }
        }
    }

    // Método para validar los datos ingresados por el usuario
    private fun isDataValid(): Boolean {
        val userData = binding.editUser.text.toString().trim()
        val userPass = binding.editPassword.text.toString().trim()

        var isValid = true
        val errorMessage = "Este campo es obligatorio" // Mensaje de error genérico

        // Validar que el nombre de usuario o correo no esté vacío
        if (userData.isEmpty()) {
            binding.editUser.error = errorMessage
            isValid = false
        }

        // Validar que la contraseña no esté vacía
        if (userPass.isEmpty()) {
            binding.editPassword.error = errorMessage
            isValid = false
        }

        return isValid
    }

    // Mostrar un diálogo indicando que el inicio de sesión fue exitoso
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

    // Mostrar un diálogo indicando que hubo un error al iniciar sesión
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