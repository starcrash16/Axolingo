package com.proyecto_final.axolingo.selector_palabras

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.R
import kotlin.collections.listOf

class InterfazSelector : AppCompatActivity() {

    private var indiceActual = 0
    private lateinit var container: ViewGroup
    var progressBar: ProgressBar? = null
    val instruccion = "Traduce la siguiente frase\n"
    var respCorrectas: Int = 0

    val oracion: List<List<String>> = listOf(
        listOf("My name is Juanito"),
        listOf("Hello world"),
        listOf("The sky is blue")
    )
    val palabras: List<List<String>> = listOf(
        listOf("Mi", "nombre", "es", "Juanito", "Hola", "mundo"),
        listOf("Mi", "nombre", "es", "Juanito", "Hola", "mundo"),
        listOf("verde", "cielos", "El", "cielo", "es", "azul")
    )
    val respuesta: List<List<String>> = listOf(
        listOf("Mi nombre es Juanito"),
        listOf("Hola mundo"),
        listOf("El cielo es azul")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_selector)
        container = findViewById<ViewGroup>(R.id.container)

        progressBar = findViewById<ProgressBar>(R.id.progressBar)
        progressBar?.max = 100
        cargarSiguientePregunta()
    }

    private fun cargarSiguientePregunta() {
        if (indiceActual >= palabras.size) {
            Toast.makeText(this, "¡Completaste todas las frases!", Toast.LENGTH_LONG).show()
            container.removeAllViews()
            val texto = "Aciertos: $respCorrectas de $indiceActual"
            val inflater = layoutInflater
            val puntuacion: View = inflater.inflate(R.layout.puntuacion_final, null)
            puntuacion.findViewById<TextView>(R.id.text).text = texto
            puntuacion.findViewById<Button>(R.id.exitButton ).setOnClickListener {
                finish()
            }
            container.addView(puntuacion)
            return
        }

        container.removeAllViews()

        val control = ControlSelector(this)
        control.background = getDrawable(R.drawable.edittext_form)
        control.instrucciones = instruccion + oracion[indiceActual].joinToString(" ")
        control.respuesta = respuesta[indiceActual].joinToString(" ")
        control.cargarBancoDePalabras(palabras[indiceActual])

        control.setComprobarListener {
            if (control.comprobarRespuesta()) {
                Toast.makeText(this, "Correcto", Toast.LENGTH_SHORT).show()
                respCorrectas++
            } else {
                Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show()
            }
            indiceActual++
            progressBar?.progress = 100 / palabras.size * indiceActual
            cargarSiguientePregunta()
        }
        container.addView(control)
    }
}