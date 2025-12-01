package com.proyecto_final.axolingo.userSignForms

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

// Actividad para gestionar el registro de nuevos usuarios
// Permite a los usuarios crear una cuenta con su correo electrónico y contraseña utilizando Firebase Authentication.
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding // Enlace a la vista de la actividad
    private lateinit var firebaseAuth: FirebaseAuth // Instancia de Firebase Authentication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance() // Inicializa Firebase Authentication

        // Configura el enlace para ir a la actividad de inicio de sesión
        binding.textView.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
        }

        // Configura el botón para registrar un nuevo usuario
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString()
            val pass = binding.passET.text.toString()
            val confirmPass = binding.confirmPassEt.text.toString()

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()) {
                if (pass == confirmPass) {
                    // Intenta registrar al usuario con Firebase Authentication
                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent) // Redirige al usuario a la actividad de inicio de sesión
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show() // Muestra un error
                        }
                    }
                } else {
                    Toast.makeText(this, "Password is not matching", Toast.LENGTH_SHORT).show() // Las contraseñas no coinciden
                }
            } else {
                Toast.makeText(this, "Empty Fields Are not Allowed !!", Toast.LENGTH_SHORT).show() // Campos vacíos no permitidos
            }
        }
    }
}