package com.proyecto_final.axolingo.pizarra

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.proyecto_final.axolingo.R
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.random.Random

// Actividad que representa la interfaz de la pizarra mágica
class InterfazPizarra : AppCompatActivity() {
    private var indiceActual = 0 // Índice del ejercicio actual
    private lateinit var container: ViewGroup // Contenedor de vistas dinámicas
    var progressBar: ProgressBar? = null // Barra de progreso
    val instruccion = "Completa la siguiente operacion\n" // Instrucción para el usuario
    var respCorrectas: Int = 0 // Contador de respuestas correctas

    // Lista de ejercicios generados aleatoriamente
    val ejercicios: List<List<Int>> = List(3) {
        val num1 = Random.nextInt(5, 11)
        val num2 = Random.nextInt(5)
        listOf(num1, num2)
    }
    val operador: Array<String> = arrayOf("+", "-") // Operadores matemáticos
    val numero_ejercicios = 3 // Número total de ejercicios

    // Listas para almacenar preguntas y respuestas generadas
    private val generatedOperations = mutableListOf<String>()
    private val generatedResults = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_selector)
        container = findViewById(R.id.container)
        progressBar = findViewById(R.id.progressBar)
        progressBar?.max = 100
        cargarSiguientePregunta() // Cargar el primer ejercicio
    }

    // Carga la siguiente pregunta o muestra la puntuación final
    private fun cargarSiguientePregunta() {
        if (indiceActual >= numero_ejercicios) {
            Toast.makeText(this, "¡Completaste todas las frases!", Toast.LENGTH_LONG).show()
            container.removeAllViews()

            val finalScore = respCorrectas.toFloat() / 3.0f // Calcula la puntuación final
            val sessionManager = SessionManager(applicationContext)
            val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()

            lifecycleScope.launch(Dispatchers.IO) {
                val username = sessionManager.loginFlow.first()
                if (username != null) {
                    userDao.updateSCBoard(username, finalScore) // Guarda la puntuación en la base de datos
                }
            }

            val texto = "Aciertos: $respCorrectas de $indiceActual"
            val inflater = layoutInflater
            val puntuacion: View = inflater.inflate(R.layout.puntuacion_final, null)
            puntuacion.findViewById<TextView>(R.id.text).text = texto
            puntuacion.findViewById<Button>(R.id.exitButton ).setOnClickListener {
                finish()
            }
            puntuacion.findViewById<Button>(R.id.btnFeedback).setOnClickListener {
                showFeedbackDialog() // Muestra un diálogo con las respuestas correctas
            }
            container.addView(puntuacion)
            return
        }

        container.removeAllViews()

        val control = PizarraMagica(this)
        control.background = getDrawable(R.drawable.edittext_form)
        val num_op = Random.nextInt(2)

        val operationString = "${ejercicios[indiceActual][0]} ${operador[num_op]} ${ejercicios[indiceActual][1]}"
        control.instrucciones = instruccion + operationString

        var resultString = ""
        when (num_op) {
            0 -> resultString = (ejercicios[indiceActual][0] + ejercicios[indiceActual][1]).toString()
            1 -> resultString = (ejercicios[indiceActual][0] - ejercicios[indiceActual][1]).toString()
        }
        control.respuesta = resultString

        // Almacena las operaciones y resultados generados
        generatedOperations.add(operationString)
        generatedResults.add(resultString)

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

    // Muestra un diálogo con las respuestas correctas
    private fun showFeedbackDialog() {
        val builder = StringBuilder()
        for (i in generatedOperations.indices) {
            builder.append("${i + 1}. ${generatedOperations[i]}\nR = ${generatedResults[i]}\n\n")
        }

        AlertDialog.Builder(this)
            .setTitle("Respuestas Correctas")
            .setMessage(builder.toString())
            .setPositiveButton("Cerrar") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
}