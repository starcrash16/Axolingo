package com.proyecto_final.axolingo.forms

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.data.entity.User
import com.proyecto_final.axolingo.databinding.RegistroBinding

class Registrarse : AppCompatActivity(){
    private lateinit var binding: RegistroBinding
    private lateinit var registroViewModel: RegistroViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDao = AppDatabase.getDatabase(applicationContext).userDao()
        registroViewModel = RegistroViewModel(userDao)
        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.btnRegister.setOnClickListener {
            if (isDataValid()) {
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
                            showSuccessDialog()
                        }
                    },
                    onConflict = {
                        runOnUiThread {
                            showConflictDialog()
                        }
                    }
                )
            }
        }
    }

    private fun isDataValid(): Boolean {
        val userName = binding.editUser.text.toString().trim()
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()
        val confirmPassword = binding.editConfPass.text.toString().trim()

        var isValid = true
        val errorMessage = "Este campo es obligatorio"

        if (userName.isEmpty()) {
            binding.editUser.error = errorMessage
            isValid = false
        }

        if (email.isEmpty()) {
            binding.editEmail.error = errorMessage
            isValid = false
        }

        if (password.isEmpty()) {
            binding.editPassword.error = errorMessage
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.editConfPass.error = errorMessage
            isValid = false
        }

        if (isValid && password != confirmPassword) {
            binding.editPassword.error = "Las contraseñas no coinciden"
            binding.editConfPass.text.clear()
            isValid = false
        }

        return isValid
    }

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