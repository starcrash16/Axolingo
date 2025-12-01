package com.proyecto_final.axolingo.leccion_ingles

import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.InputStreamReader
import kotlin.random.Random

// Actividad para el juego de Spelling Bee
class SpellingBeeActivity : BaseActivity() {

    // Elementos de la interfaz de usuario
    private lateinit var sourceZone: ViewGroup // Zona de letras disponibles
    private lateinit var answerZone: LinearLayout // Zona de respuesta
    private lateinit var wordToSpellText: TextView // Texto con la palabra a deletrear
    private lateinit var btnValidate: Button // Botón para validar la respuesta
    private lateinit var progressBar: ProgressBar // Barra de progreso del juego
    private lateinit var tvGameFinished: TextView // Texto que indica el final del juego
    private lateinit var btnBackToMenu: Button // Botón para volver al menú

    // Variables de estado del juego
    private var results: MutableList<Boolean> = mutableListOf() // Resultados de las palabras
    private val gameWords = listOf("AXO", "CAT", "DOG", "SUN") // Palabras del juego
    private var currentWordIndex: Int = 0 // Índice de la palabra actual
    private val currentWord: String
        get() = if (gameWords.isNotEmpty() && currentWordIndex < gameWords.size) gameWords[currentWordIndex] else ""

    // Constantes para la generación de letras
    private val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" // Alfabeto
    private val NUM_DISTRACTORS = 10 // Número de letras distractoras

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spelling_bee)

        // Inicializar componentes de la interfaz de usuario
        sourceZone = findViewById(R.id.sourceZone)
        answerZone = findViewById(R.id.answerZone)
        wordToSpellText = findViewById(R.id.wordToSpellText)
        btnValidate = findViewById(R.id.btnValidate)
        progressBar = findViewById(R.id.progressBar)
        tvGameFinished = findViewById(R.id.tvGameFinished)
        btnBackToMenu = findViewById(R.id.btnBackToMenu)

        // Configurar listeners para las zonas de arrastre
        answerZone.setOnDragListener(dragListener)
        sourceZone.setOnDragListener(dragListener)

        // Configurar listeners para los botones
        btnValidate.setOnClickListener { onValidateClicked() }
        findViewById<Button>(R.id.btnRestart).setOnClickListener { resetCurrentWord() }
        btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MenuLeccionInglesActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupGame() // Configurar el juego
    }

    // Configura el nivel/palabra actual y genera el banco de letras
    private fun setupGame() {
        if (currentWordIndex >= gameWords.size) {
            showGameFinished() // Mostrar mensaje de finalización
            return
        }

        // Actualizar el contador de progreso
        progressBar.max = gameWords.size
        progressBar.progress = currentWordIndex
        wordToSpellText.text = "Deletrea: '${currentWord}'"

        // Reiniciar el estado de la interfaz
        sourceZone.removeAllViews()
        answerZone.removeAllViews()
        answerZone.setBackgroundResource(R.drawable.answer_zone_bg)
        sourceZone.setBackgroundColor(Color.TRANSPARENT)
        btnValidate.isEnabled = true

        // Generar letras objetivo y distractoras
        val targetLetters = currentWord.toList().map { it.toString() }.toMutableList()
        val distractors = (1..NUM_DISTRACTORS).map {
            ALPHABET[Random.nextInt(ALPHABET.length)].toString()
        }

        // Combinar y mezclar todas las letras
        val allLetters = (targetLetters + distractors).shuffled()

        // Crear y agregar TextViews a la zona de letras disponibles
        for (letter in allLetters) {
            sourceZone.addView(createDraggableLetter(letter))
        }
    }

    // Crea un TextView configurado para arrastrar
    private fun createDraggableLetter(letter: String): TextView {
        val marginInPixels = 5
        val warmColors = listOf(
            Color.parseColor("#FFB74D"), // Naranja
            Color.parseColor("#FFD54F"), // Amarillo
            Color.parseColor("#FF8A65"), // Rojo claro
            Color.parseColor("#4FC3F7"), // Azul claro
            Color.parseColor("#81C784"), // Verde claro
            Color.parseColor("#BA68C8"), // Morado
            Color.parseColor("#F06292"), // Rosa
            Color.parseColor("#FFF176"), // Limón
            Color.parseColor("#A1887F"), // Marrón
            Color.parseColor("#E57373")  // Rojo
        )
        val randomColor = warmColors.random()
        val textView = TextView(this).apply {
            text = letter
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(randomColor)
            setBackgroundResource(R.drawable.letter_bg)
            layoutParams = LinearLayout.LayoutParams(150, 150).also {
                it.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
            }
        }

        // Configurar el listener para iniciar el arrastre
        textView.setOnLongClickListener { view ->
            val clipData = ClipData.newPlainText("letter", letter)
            val shadowBuilder = View.DragShadowBuilder(view)
            view.startDragAndDrop(clipData, shadowBuilder, view, 0)
            view.visibility = View.INVISIBLE
            true
        }
        return textView
    }

    // Verifica si las letras en la zona de respuesta coinciden con la palabra objetivo
    private fun onValidateClicked() {
        val spelledWord = StringBuilder()
        for (i in 0 until answerZone.childCount) {
            val child = answerZone.getChildAt(i)
            if (child is TextView) {
                spelledWord.append(child.text)
            }
        }

        val result = spelledWord.toString()
        results.add(result == currentWord) // Comparar con la palabra objetivo
        currentWordIndex++
        setupGame() // Configurar el siguiente nivel
    }

    // Reinicia la palabra actual devolviendo las letras a la zona de origen
    private fun resetCurrentWord() {
        val lettersToReturn = mutableListOf<View>()
        for (i in 0 until answerZone.childCount) {
            lettersToReturn.add(answerZone.getChildAt(i))
        }
        for (view in lettersToReturn) {
            answerZone.removeView(view)
            sourceZone.addView(view)
        }
        answerZone.setBackgroundResource(R.drawable.answer_zone_bg)
        sourceZone.setBackgroundColor(Color.TRANSPARENT)
    }

    // Muestra el mensaje de finalización del juego
    private fun showGameFinished() {
        progressBar.visibility = View.GONE
        wordToSpellText.visibility = View.GONE
        answerZone.visibility = View.GONE
        sourceZone.visibility = View.GONE
        findViewById<Button>(R.id.btnValidate).visibility = View.GONE
        findViewById<Button>(R.id.btnRestart).visibility = View.GONE
        findViewById<View>(R.id.dottedLine).visibility = View.GONE

        val score = results.count { it }
        val finalScore = score.toFloat() / 3.0f

        val sessionManager = SessionManager(applicationContext)
        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val username = sessionManager.loginFlow.first()
            if (username != null) {
                userDao.updateSCSpell(username, finalScore)
            }
        }
        tvGameFinished.text = "¡Felicidades, Juego Terminado!\nAciertos: $score/${gameWords.size}"
        tvGameFinished.visibility = View.VISIBLE
        btnBackToMenu.visibility = View.VISIBLE
    }

    // Listener para eventos de arrastre en ambas zonas
    private val dragListener = View.OnDragListener { view, event ->
        if (view !is ViewGroup) {
            return@OnDragListener true
        }
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            DragEvent.ACTION_DROP -> {
                val originalView = event.localState as View
                val owner = originalView.parent as ViewGroup

                owner.removeView(originalView)
                val destination = view

                var insertIndex = 0
                for (i in 0 until destination.childCount) {
                    val child = destination.getChildAt(i)
                    if (event.x > child.left + child.width / 2) {
                        insertIndex = i + 1
                    }
                }

                destination.addView(originalView, insertIndex)
                originalView.visibility = View.VISIBLE

                if (view.id == R.id.answerZone) {
                    view.setBackgroundResource(R.drawable.answer_zone_bg)
                } else {
                    view.setBackgroundColor(Color.TRANSPARENT)
                }
                true
            }
            else -> true
        }
    }
}