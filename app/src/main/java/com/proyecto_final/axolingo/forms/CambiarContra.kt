package com.proyecto_final.axolingo.forms

import android.app.AlertDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.databinding.ContrasenaBinding

class CambiarContra : AppCompatActivity() {
    private lateinit var binding: ContrasenaBinding
    private lateinit var chPassViewModel: ContraViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        chPassViewModel = ContraViewModel(userDao)
        binding = ContrasenaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnContinue.setOnClickListener {
            if (isDataValid()) {
                val user = binding.editUser.text.toString().trim()
                val newPass = binding.editPassword.text.toString().trim()
                chPassViewModel.cambiarContra(user, newPass,
                    onSuccess = {
                        runOnUiThread {
                            showSuccessDialog()
                        }
                    },
                    onConflict = {
                        runOnUiThread {
                            showConflictDialog()
                        }
                    })
            }
        }
    }

    private fun isDataValid(): Boolean {
        val userName = binding.editUser.text.toString().trim()
        val newPass = binding.editPassword.text.toString().trim()
        val newPassConf = binding.editConfPass.text.toString().trim()

        var isValid = true
        val errorMessage = "Este campo es obligatorio"

        if (userName.isEmpty()) {
            binding.editUser.error = errorMessage
            isValid = false
        }

        if (newPass.isEmpty()) {
            binding.editPassword.error = errorMessage
            isValid = false
        }

        if (newPassConf.isEmpty()) {
            binding.editConfPass.error = errorMessage
            isValid = false
        }

        if (isValid && newPass != newPassConf) {
            binding.editPassword.error = "Las contraseñas no coinciden"
            binding.editConfPass.text.clear()
            isValid = false
        }

        return isValid
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cambio exitoso")
            .setMessage("Se cambió la contraseña")
            .setPositiveButton("Aceptar") { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

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