package com.proyecto_final.axolingo.art.dialogs

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto_final.axolingo.R
import kotlin.random.Random

data class Joke(
    val chiste_entrada: String,
    val chiste_final: String
)

class JokeDialog(private val context: Context) {

    fun show() {
        // Cargar JSON de chistes
        val jokes = loadJokes()
        if (jokes.isEmpty()) {
            return
        }

        // Seleccionar un chiste aleatorio
        val randomJoke = jokes[Random.nextInt(jokes.size)]

        // Crear un FrameLayout para el fondo oscuro
        val containerLayout = FrameLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Fondo oscuro (overlay)
        val darkOverlay = View(context).apply {
            setBackgroundColor(0x80000000.toInt()) // Negro con transparencia
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        // Contenedor del diálogo personalizado
        val dialogContent = createDialogContent(randomJoke)

        containerLayout.addView(darkOverlay)
        containerLayout.addView(dialogContent)

        // Crear AlertDialog
        val builder = AlertDialog.Builder(context, android.R.style.Theme_Translucent_NoTitleBar)
        builder.setView(containerLayout)
        
        val dialog = builder.create()
        dialog.window?.apply {
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        // Guardar referencia del dialog para cerrarlo desde el botón
        val buttonReveal = dialogContent.findViewById<Button>(R.id.btn_chiste)
        val punchlineTextView = dialogContent.findViewById<TextView>(R.id.tv_punchline)
        
        // Variable para rastrear si el punchline ya fue mostrado
        var isPunchlineShown = false
        
        buttonReveal.setOnClickListener {
            if (!isPunchlineShown) {
                // Primera presión: mostrar el punchline
                punchlineTextView.visibility = View.VISIBLE
                val fadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
                punchlineTextView.startAnimation(fadeIn)
                buttonReveal.text = "Cerrar"
                isPunchlineShown = true
            } else {
                // Segunda presión: cerrar el dialog
                dialog.dismiss()
            }
        }

        // Animación de entrada
        val scaleIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
        dialogContent.startAnimation(scaleIn)

        dialog.show()
    }

    private fun createDialogContent(joke: Joke): ConstraintLayout {
        val layout = ConstraintLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                (context.resources.displayMetrics.widthPixels * 0.85).toInt(), // 85% del ancho de la pantalla
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER
            )
            setBackgroundResource(R.drawable.dialog_background)
            elevation = 8f
            setPadding(24, 24, 24, 24)
        }

        layout.id = View.generateViewId()

        // Texto del chiste (entrada)
        val jokeTextView = TextView(context).apply {
            id = View.generateViewId()
            text = joke.chiste_entrada
            textSize = 18f
            setTextColor(context.getColor(android.R.color.black))
            gravity = Gravity.CENTER
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = 0
            }
        }

        // Texto del punchline (respuesta)
        val punchlineTextView = TextView(context).apply {
            id = R.id.tv_punchline
            text = joke.chiste_final
            textSize = 16f
            setTextColor(context.getColor(android.R.color.darker_gray))
            gravity = Gravity.CENTER
            visibility = View.GONE // Inicialmente oculto
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToBottom = jokeTextView.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = 16
            }
        }

        // Botón para revelar la respuesta
        val revealButton = Button(context).apply {
            id = R.id.btn_chiste
            text = "Ver Respuesta"
            layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                topToBottom = punchlineTextView.id
                startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                topMargin = 24
                bottomMargin = 0
            }
        }

        layout.addView(jokeTextView)
        layout.addView(punchlineTextView)
        layout.addView(revealButton)

        return layout
    }

    private fun loadJokes(): List<Joke> {
        return try {
            val inputStream = context.resources.openRawResource(R.raw.chistes)
            val jsonString = inputStream.reader().readText()
            val type = object : TypeToken<List<Joke>>() {}.type
            Gson().fromJson(jsonString, type)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
