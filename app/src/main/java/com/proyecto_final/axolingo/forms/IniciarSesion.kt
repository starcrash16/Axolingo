package com.proyecto_final.axolingo.forms

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.databinding.InicioSesionBinding
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager


class IniciarSesion : BaseActivity(){
    private lateinit var binding: InicioSesionBinding
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()
        val sessionManager = SessionManager(applicationContext)
        loginViewModel = LoginViewModel(userDao, sessionManager)
        binding = InicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.forgotPassword.setOnClickListener {
            val intent = Intent(this, CambiarContra::class.java)
            startActivity(intent)
        }

        binding.regButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }

        binding.btnLogin.setOnClickListener {
            if (isDataValid()) {
                val userData = binding.editUser.text.toString().trim()
                val userPass = binding.editPassword.text.toString().trim()
                loginViewModel.loginUsuario(userData, userPass,
                    onSuccess = { user ->
                        runOnUiThread {
                            showSuccessDialog(user.user)
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
        val userData = binding.editUser.text.toString().trim()
        val userPass = binding.editPassword.text.toString().trim()

        var isValid = true
        val errorMessage = "Este campo es obligatorio"

        if (userData.isEmpty()) {
            binding.editUser.error = errorMessage
            isValid = false
        }

        if (userPass.isEmpty()) {
            binding.editPassword.error = errorMessage
            isValid = false
        }

        return isValid
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