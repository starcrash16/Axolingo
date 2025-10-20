package com.proyecto_final.axolingo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.views.MenuPrincipal

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var lienzo = MenuPrincipal(applicationContext)
        setContentView(lienzo)
    }

    /*
            setContentView(R.layout.)

    * */
}