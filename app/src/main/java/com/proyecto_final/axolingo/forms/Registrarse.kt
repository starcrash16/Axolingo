package com.proyecto_final.axolingo.forms

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.data.entity.User
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.databinding.RegistroBinding

// Actividad para manejar el registro de nuevos usuarios
class Registrarse : BaseActivity(){
    private lateinit var binding: RegistroBinding // Enlace con el diseño XML
    private lateinit var registroViewModel: RegistroViewModel // ViewModel para manejar la lógica de registro

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del DAO y ViewModel
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        registroViewModel = RegistroViewModel(userDao)
        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del botón para registrar un nuevo usuario
        binding.btnRegister.setOnClickListener {
            if (isDataValid()) { // Verificar si los datos ingresados son válidos
                val user = User(
                    uid = 0,
                    user = binding.editUser.text.toString().trim(),
                    email = binding.editEmail.text.toString().trim(),
                    password = binding.editPassword.text.toString().trim(),
                    sc_spell = 0.0f,
                    sc_reading = 0.0f,
                    sc_vocab = 0.0f,
                    sc_transl = 0.0f,
                    sc_board = 0.0f,
                    sc_shapes = 0.0f
                )
                registroViewModel.registrarUsuario(
                    user,
                    onSuccess = {
                        runOnUiThread {
                            showSuccessDialog() // Mostrar diálogo de éxito
                        }
                    },
                    onConflict = {
                        runOnUiThread {
                            showConflictDialog() // Mostrar diálogo de error
                        }
                    }
                )
            }
        }
    }

    // Método para validar los datos ingresados por el usuario
    private fun isDataValid(): Boolean {
        val userName = binding.editUser.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()
        val confirmPassword = binding.editConfPass.text.toString().trim()

        var isValid = true
        val errorMessage = "Este campo es obligatorio" // Mensaje de error genérico

        // Validar que el nombre de usuario no esté vacío
        if (userName.isEmpty()) {
            binding.editUser.error = errorMessage
            isValid = false
        }

        // Validar que el correo electrónico no esté vacío
        if (email.isEmpty()) {
            binding.editEmail.error = errorMessage
            isValid = false
        }

        // Validar que la contraseña no esté vacía
        if (password.isEmpty()) {
            binding.editPassword.error = errorMessage
            isValid = false
        }

        // Validar que la confirmación de la contraseña no esté vacía
        if (confirmPassword.isEmpty()) {
            binding.editConfPass.error = errorMessage
            isValid = false
        }

        // Validar que las contraseñas coincidan
        if (isValid && password != confirmPassword) {
            binding.editPassword.error = "Las contraseñas no coinciden"
            binding.editConfPass.text.clear()
            isValid = false
        }

        return isValid
    }

    // Mostrar un diálogo indicando que el registro fue exitoso
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Registro exitoso")
            .setMessage("Cuenta registrada correctamente, inicie sesion")
            .setPositiveButton("Iniciar Sesión") { dialog, _ ->
                dialog.dismiss()
                val intent = Intent(this, IniciarSesion::class.java)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Mostrar un diálogo indicando que el usuario ya está registrado
    private fun showConflictDialog() {
        AlertDialog.Builder(this)
            .setTitle("Usuario ya registrado")
            .setMessage("Nombre de usuario registrado, elija otro")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}