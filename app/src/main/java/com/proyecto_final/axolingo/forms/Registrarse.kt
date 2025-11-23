package com.proyecto_final.axolingo.forms

import android.os.Bundle
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.databinding.RegistroBinding

class Registrarse : BaseActivity(){
    private lateinit var binding: RegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = RegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}