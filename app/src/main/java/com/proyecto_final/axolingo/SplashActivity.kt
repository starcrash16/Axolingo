package com.proyecto_final.axolingo

import android.animation.Animator
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.menu_principal.MenuPrincipalActivity
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.launch

// SplashActivity: pantalla de presentación que muestra una animación y decide
// la siguiente pantalla según el estado de sesión del usuario.
// - Si hay un usuario logueado, va a `MenuPrincipalActivity`.
// - Si no hay usuario, va a `MainActivity`.
class SplashActivity : AppCompatActivity() {
    // Gestor de sesión para decidir a qué actividad ir
    private lateinit var sessionManager: SessionManager
    // ImageView que muestra el logo animado
    private lateinit var myImageView: ImageView
    // AnimatorSet que controla la animación de la splash
    private lateinit var loadingAnimator: AnimatorSet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        myImageView = findViewById(R.id.logo)

        // Carga la animación definida en XML y la vincula al ImageView
        loadingAnimator = AnimatorInflater.loadAnimator(this, R.animator.scale_animation) as AnimatorSet
        loadingAnimator.setTarget(myImageView)
        loadingAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                // Reinicia la animación cuando termina para un efecto continuo
                loadingAnimator.start()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        loadingAnimator.start()

        // Observa el flujo de sesión y redirige según si hay usuario autenticado
        sessionManager = SessionManager(applicationContext)
        lifecycleScope.launch {
            sessionManager.loginFlow.collect { username ->
                val targetActivity = if (username != null) {
                    MenuPrincipalActivity::class.java
                } else {
                    MainActivity::class.java
                }
                // Cancelar la animación antes de navegar
                loadingAnimator.cancel()
                val intent = Intent(this@SplashActivity, targetActivity)
                startActivity(intent)
                finish()
                return@collect
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Asegura que la animación esté cancelada para evitar fugas
        if (loadingAnimator.isRunning) {
            loadingAnimator.cancel()
        }
    }
}