package com.proyecto_final.axolingo.selector_palabras

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.BaseActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.InputStream

// Actividad que representa la interfaz para ejercicios de traducción
class InterfazSelector : BaseActivity() {

    private var indiceActual = 0 // Índice del ejercicio actual
    private lateinit var container: ViewGroup // Contenedor de vistas dinámicas
    var progressBar: ProgressBar? = null // Barra de progreso
    val instruccion = "Traduce la siguiente frase\n" // Instrucción para el usuario
    var respCorrectas: Int = 0 // Contador de respuestas correctas

    lateinit var ejercicios: List<Ejercicio> // Lista de ejercicios cargados
    val numero_ejercicios = 5 // Número total de ejercicios

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_selector)
        container = findViewById(R.id.container)

        // Carga los ejercicios desde un archivo JSON
        var listaEjercicios: List<Ejercicio> = cargarJSON(this)
        ejercicios = listaEjercicios.shuffled().take(numero_ejercicios)
        progressBar = findViewById(R.id.progressBar)
        progressBar?.max = 100
        cargarSiguientePregunta() // Cargar el primer ejercicio
    }

    // Carga los ejercicios desde un archivo JSON
    private fun cargarJSON(context: Context): List<Ejercicio> {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.palabras_traduccion)
        val jsonString = inputStream.bufferedReader().use { it.readText() }
        val gson = Gson()
        val tipoLista = object: TypeToken<List<Ejercicio>>() {}.type
        return gson.fromJson(jsonString, tipoLista)
    }

    // Carga la siguiente pregunta o muestra la puntuación final
    private fun cargarSiguientePregunta() {
        if (indiceActual >= numero_ejercicios) {
            Toast.makeText(this, "¡Completaste todas las frases!", Toast.LENGTH_LONG).show()
            container.removeAllViews()

            val finalScore = respCorrectas.toFloat() / 3.0f // Calcula la puntuación final
            val sessionManager = SessionManager(applicationContext)
            val userDao = AppDatabase.getDatabase(applicationContext).userDao()

            lifecycleScope.launch(Dispatchers.IO) {
                val username = sessionManager.loginFlow.first()
                if (username != null) {
                    userDao.updateSCTransl(username, finalScore) // Guarda la puntuación en la base de datos
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

        val control = ControlSelector(this)
        control.background = getDrawable(R.drawable.edittext_form)
        control.instrucciones = instruccion + ejercicios[indiceActual].sentence
        control.respuesta = ejercicios[indiceActual].answer
        control.cargarBancoDePalabras(ejercicios[indiceActual].words)

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
        var index = 1
        for (ejercicio in ejercicios) {
            builder.append("$index. ${ejercicio.sentence}\nR = ${ejercicio.answer}\n\n")
            index++
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