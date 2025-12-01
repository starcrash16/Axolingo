package com.proyecto_final.axolingo.forms

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.databinding.ContrasenaBinding

// Actividad para cambiar la contraseña de un usuario
class CambiarContra : AppCompatActivity() {
    private lateinit var binding: ContrasenaBinding // Enlace con el diseño XML
    private lateinit var chPassViewModel: ContraViewModel // ViewModel para manejar la lógica de cambio de contraseña

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización del DAO y ViewModel
        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()
        chPassViewModel = ContraViewModel(userDao)
        binding = ContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configuración del botón para continuar con el cambio de contraseña
        binding.btnContinue.setOnClickListener {
            if (isDataValid()) { // Verificar si los datos ingresados son válidos
                val user = binding.editUser.text.toString().trim()
                val newPass = binding.editPassword.text.toString().trim()
                chPassViewModel.cambiarContra(user, newPass,
                    onSuccess = {
                        runOnUiThread {
                            showSuccessDialog() // Mostrar diálogo de éxito
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
        val userName = binding.editUser.text.toString().trim()
        val newPass = binding.editPassword.text.toString().trim()
        val newPassConf = binding.editConfPass.text.toString().trim()

        var isValid = true
        val errorMessage = "Este campo es obligatorio" // Mensaje de error genérico

        // Validar que el nombre de usuario no esté vacío
        if (userName.isEmpty()) {
            binding.editUser.error = errorMessage
            isValid = false
        }

        // Validar que la nueva contraseña no esté vacía
        if (newPass.isEmpty()) {
            binding.editPassword.error = errorMessage
            isValid = false
        }

        // Validar que la confirmación de la contraseña no esté vacía
        if (newPassConf.isEmpty()) {
            binding.editConfPass.error = errorMessage
            isValid = false
        }

        // Validar que las contraseñas coincidan
        if (isValid && newPass != newPassConf) {
            binding.editPassword.error = "Las contraseñas no coinciden"
            binding.editConfPass.text.clear()
            isValid = false
        }

        return isValid
    }

    // Mostrar un diálogo indicando que el cambio de contraseña fue exitoso
    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cambio exitoso")
            .setMessage("Se cambió la contraseña")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                finish() // Finalizar la actividad
            }
            .show()
    }

    // Mostrar un diálogo indicando que hubo un error al cambiar la contraseña
    private fun showConflictDialog() {
        AlertDialog.Builder(this)
            .setTitle("Fallo al cambiar contraseña")
            .setMessage("Usuario no encontrado o error al cambiar la contraseña")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}