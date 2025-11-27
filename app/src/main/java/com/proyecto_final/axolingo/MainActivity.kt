package com.proyecto_final.axolingo

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.art.botons.BotonMenuPrincipal
import com.proyecto_final.axolingo.art.botons.BotonMenuPrincipalAzul
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.forms.IniciarSesion
import com.proyecto_final.axolingo.forms.LoginViewModel
import com.proyecto_final.axolingo.forms.Registrarse
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.launch

// Asegúrate de que tu layout de bienvenida se llama 'menu_bienvenida.xml'
// y que contiene un botón con el id 'btnInfoApp'.

class MainActivity : BaseActivity() {
    private lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Establece el layout para esta Activity.
        setContentView(R.layout.menu_bienvenida)

        // 2. Encuentra el botón por su ID.
        val localButton: BotonMenuPrincipalAzul = findViewById(R.id.btnIniciarLocal)
        val infoAppButton: BotonMenuPrincipalAzul = findViewById(R.id.btnInfoApp)
        val loginButton:  BotonMenuPrincipal = findViewById(R.id.btnIniciarSesion)
        val signUpButton: BotonMenuPrincipal = findViewById(R.id.btnRegistro)

        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()
        val sessionManager = SessionManager(applicationContext)
        loginViewModel = LoginViewModel(userDao, sessionManager)

        // 3. Configura el listener para que reaccione al clic del usuario.
        localButton.setOnClickListener {
            loginViewModel.loginUsuario("local", "local",
                onSuccess = {
                    runOnUiThread {
                        // 4. Crea un Intent para iniciar la Activity correcta.
                        //    La corrección clave está en usar '::class.java'.
                        val intent = Intent(this, MenuPrincipalActivity::class.java)

                        // 5. Inicia la nueva Activity.
                        startActivity(intent)
                    }
                },
                onConflict = {
                    runOnUiThread {
                        AlertDialog.Builder(this)
                            .setTitle("Fallo al iniciar sesión")
                            .setMessage("Fallo al iniciar de forma local. Inténtelo de nuevo")
                            .setPositiveButton("Aceptar") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .show()
                    }
                })
        }

        infoAppButton.setOnClickListener {
            //pantalla de informacion
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, IniciarSesion::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, Registrarse::class.java)
            startActivity(intent)
        }
    }
}

