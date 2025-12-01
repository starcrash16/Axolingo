package com.proyecto_final.axolingo.userSignForms

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.proyecto_final.axolingo.MainActivity

// Actividad para gestionar el inicio de sesión de usuarios
// Permite a los usuarios ingresar con su correo electrónico y contraseña utilizando Firebase Authentication.
class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding // Enlace a la vista de la actividad
    private lateinit var firebaseAuth: FirebaseAuth // Instancia de Firebase Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // Inicializa Firebase Authentication

        // Configura el enlace para ir a la actividad de registro
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Configura el botón para iniciar sesión
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                // Intenta iniciar sesión con Firebase Authentication
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent) // Redirige al usuario a la actividad principal
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() // Muestra un error
                    }
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show() // Campos vacíos no permitidos
            }
        }
    }

    override fun onStart() {
        super.onStart()

        // Verifica si el usuario ya está autenticado
        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Redirige al usuario a la actividad principal
        }
    }
}