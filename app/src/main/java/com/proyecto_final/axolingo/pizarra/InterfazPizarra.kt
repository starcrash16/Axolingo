package com.proyecto_final.axolingo.pizarra

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.R
import kotlin.random.Random

class InterfazPizarra : AppCompatActivity() {
    private var indiceActual = 0
    private lateinit var container: ViewGroup
    var progressBar: ProgressBar? = null
    val instruccion = "Completa la siguiente operacion\n"
    var respCorrectas: Int = 0

    val ejercicios: List<List<Int>> = List(3) {
        val num1 = Random.nextInt(5, 11)
        val num2 = Random.nextInt(5)
        listOf(num1, num2)
    }
    val operador: Array<String> = arrayOf("+", "-")
    val numero_ejercicios = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_selector)
        container = findViewById(R.id.container)
        progressBar = findViewById(R.id.progressBar)
        progressBar?.max = 100
        cargarSiguientePregunta()
    }

    private fun cargarSiguientePregunta() {
        if (indiceActual >= numero_ejercicios) {
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

        val control = PizarraMagica(this)
        control.background = getDrawable(R.drawable.edittext_form)
        val num_op = Random.nextInt(2)
        control.instrucciones = instruccion + ejercicios[indiceActual][0] + operador[num_op] + ejercicios[indiceActual][1]
        when (num_op) {
            0 -> control.respuesta = (ejercicios[indiceActual][0] + ejercicios[indiceActual][1]).toString()
            1 -> control.respuesta = (ejercicios[indiceActual][0] - ejercicios[indiceActual][1]).toString()
        }
        val respuestas: List<List<Int>> = List(3) { i ->
            var r1 = 0
            when (num_op) {
                0 -> r1 = ejercicios[i][0] + ejercicios[i][1]
                1 -> r1 = ejercicios[i][0] - ejercicios[i][1]
            }
            val r2 = Random.nextInt(r1)
            val r3 = Random.nextInt(r1+1, 16)
            listOf(r1, r2, r3)
        }
        control.cargarBancoDePalabras(respuestas[indiceActual])

        control.setComprobarListener {
            if (control.comprobarRespuesta()) {
                Toast.makeText(this, "Correcto", Toast.LENGTH_SHORT).show()
                respCorrectas++
            } else {
                Toast.makeText(this, "Incorrecto", Toast.LENGTH_SHORT).show()
            }
            indiceActual++
            progressBar?.progress = 100 / numero_ejercicios * indiceActual
            cargarSiguientePregunta()
        }
        container.addView(control)
    }
}