package com.proyecto_final.axolingo.forms

import android.content.Intent
import android.os.Bundle
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.databinding.ActivitySignUpBinding
import com.proyecto_final.axolingo.databinding.InicioSesionBinding
import com.proyecto_final.axolingo.databinding.RegistroBinding

class IniciarSesion : BaseActivity(){
    private lateinit var binding: InicioSesionBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = InicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }
    }
}


