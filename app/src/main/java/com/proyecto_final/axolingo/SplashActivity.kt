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

class SplashActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager     //instancia del gestor
    private lateinit var myImageView: ImageView
    private lateinit var loadingAnimator: AnimatorSet       //controlar la animacion

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash)
        myImageView = findViewById(R.id.logo)

        //cargando la animacion del xml
        loadingAnimator = AnimatorInflater.loadAnimator(this, R.animator.scale_animation) as AnimatorSet
        loadingAnimator.setTarget(myImageView)
        loadingAnimator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                loadingAnimator.start()     //al terminar la animacion, vuelve a iniciar
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })

        loadingAnimator.start()

        sessionManager = SessionManager(applicationContext)
        lifecycleScope.launch {
            sessionManager.loginFlow.collect { username ->
                val targetActivity = if (username != null) {
                    MenuPrincipalActivity::class.java
                } else {
                    MainActivity::class.java
                }
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
        if (loadingAnimator.isRunning) {
            loadingAnimator.cancel()
        }
    }
}