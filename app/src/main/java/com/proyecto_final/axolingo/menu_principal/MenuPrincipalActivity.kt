package com.proyecto_final.axolingo.menu_principal

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.views.MenuPrincipal

// Esta es la Activity que sirve como "pantalla" para tu vista personalizada.
class MenuPrincipalActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Creamos una instancia de tu componente de vista 'MenuPrincipal'.
        //    Le pasamos 'this' (que es la propia Activity) como el contexto necesario.
        val menuPrincipalView = MenuPrincipal(this)

        // 2. Establecemos esa vista como el contenido de toda la pantalla.
        setContentView(menuPrincipalView)
    }
}