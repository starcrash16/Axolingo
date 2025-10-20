package com.proyecto_final.axolingo

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.art.botons.BotonMenuPrincipal
import com.proyecto_final.axolingo.art.botons.BotonMenuPrincipalAzul
import com.proyecto_final.axolingo.forms.IniciarSesion
import com.proyecto_final.axolingo.forms.Registrarse
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity

// Asegúrate de que tu layout de bienvenida se llama 'menu_bienvenida.xml'
// y que contiene un botón con el id 'btnInfoApp'.

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Establece el layout para esta Activity.
        setContentView(R.layout.menu_bienvenida)

        // 2. Encuentra el botón por su ID.
        val infoAppButton: BotonMenuPrincipalAzul = findViewById(R.id.btnInfoApp)
        val loginButton:  BotonMenuPrincipal = findViewById(R.id.btnIniciarSesion)
        val signUpButton: BotonMenuPrincipal = findViewById(R.id.btnRegistro)

        // 3. Configura el listener para que reaccione al clic del usuario.
        infoAppButton.setOnClickListener {
            // 4. Crea un Intent para iniciar la Activity correcta.
            //    La corrección clave está en usar '::class.java'.
            val intent = Intent(this, MenuPrincipalActivity::class.java)

            // 5. Inicia la nueva Activity.
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)

            // 5. Inicia la nueva Activity.
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)

            // 5. Inicia la nueva Activity.
            startActivity(intent)
        }
    }
}

