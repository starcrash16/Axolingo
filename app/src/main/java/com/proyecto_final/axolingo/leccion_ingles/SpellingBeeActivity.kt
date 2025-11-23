package com.proyecto_final.axolingo.leccion_ingles

import android.content.ClipData
import android.content.ClipDescription
import android.graphics.Color
import android.os.Bundle
import android.view.DragEvent
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import java.io.InputStreamReader
import kotlin.random.Random

class SpellingBeeActivity : BaseActivity() {

    // sourceZone ahora es ViewGroup para aceptar FlexboxLayout
    private lateinit var sourceZone: ViewGroup
    private lateinit var answerZone: LinearLayout
    private lateinit var wordToSpellText: TextView
    private lateinit var btnValidate: Button
    private lateinit var progressText: TextView

    // Game state variables
    private var wordList: List<String> = emptyList()
    private var currentWordIndex: Int = 0
    private val currentWord: String
        get() = if (wordList.isNotEmpty() && currentWordIndex < wordList.size) wordList[currentWordIndex] else ""

    // Constants for letter generation
    private val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val NUM_DISTRACTORS = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spelling_bee)

        // Initialize UI components using IDs from XML
        sourceZone = findViewById(R.id.sourceZone)
        answerZone = findViewById(R.id.answerZone)
        wordToSpellText = findViewById(R.id.wordToSpellText)
        btnValidate = findViewById(R.id.btnValidate)
        progressText = findViewById(R.id.progressText)

        // Load words once at startup
        loadWordsFromAsset()

        // Apply drag listeners
        answerZone.setOnDragListener(dragListener)
        sourceZone.setOnDragListener(dragListener)

        // Set up button listeners
        btnValidate.setOnClickListener { checkSpelling() }
        findViewById<Button>(R.id.btnRestart).setOnClickListener {
            currentWordIndex = 0
            setupGame()
        }

        setupGame()
    }

    /**
     * Loads the word list from the JSON file, ensuring clean reading and explicit type casting.
     */
    private fun loadWordsFromAsset() {
        try {
            val inputStream = resources.openRawResource(R.raw.words)

            // CORRECCIÓN 1: Leer como texto para evitar problemas de codificación (BOM)
            val jsonString = inputStream.reader().readText()

            val type = object : TypeToken<List<String>>() {}.type

            // CORRECCIÓN 2: Forzar el tipo con fromJson<T> y cast para resolver errores de inferencia
            @Suppress("UNCHECKED_CAST")
            val rawWords = Gson().fromJson<List<String>>(jsonString, type) as List<String>

            // Mapeo final: Limpiar espacios y asegurar mayúsculas
            wordList = rawWords.map { it.trim().uppercase() }

        } catch (e: Exception) {
            Toast.makeText(this, "Error de JSON: ${e.message}", Toast.LENGTH_LONG).show()
            wordList = listOf("AXOLOTL", "KOTLIN", "ANDROID") // Failsafe words
        }
    }

    /**
     * Sets up the current level/word, generating the expanded letter bank.
     */
    private fun setupGame() {
        if (wordList.isEmpty() || currentWordIndex >= wordList.size) {
            wordToSpellText.text = "¡Juego Terminado!"
            progressText.text = "Nivel: Finalizado"
            sourceZone.removeAllViews()
            answerZone.removeAllViews()
            btnValidate.isEnabled = false
            return
        }

        // Update the progress counter
        progressText.text = "Nivel: ${currentWordIndex + 1} / ${wordList.size}"

        // Reset UI state
        sourceZone.removeAllViews()
        answerZone.removeAllViews()
        answerZone.setBackgroundResource(R.drawable.answer_zone_bg)
        sourceZone.setBackgroundColor(Color.TRANSPARENT)
        btnValidate.isEnabled = true

        // Display the current word hint
        wordToSpellText.text = "Deletrea: '${currentWord}'"

        // --- ENHANCEMENT: BROADENED LETTER BANK ---
        val targetLetters = currentWord.toList().map { it.toString() }.toMutableList()

        // Add 10 random distractor letters
        val distractors = (1..NUM_DISTRACTORS).map {
            ALPHABET[Random.nextInt(ALPHABET.length)].toString()
        }

        // Combine and shuffle all letters
        val allLetters = (targetLetters + distractors).shuffled()

        // Create and add TextViews to the source zone
        for (letter in allLetters) {
            sourceZone.addView(createDraggableLetter(letter))
        }
    }

    // SpellingBeeActivity.kt
    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    /**
     * Creates a TextView configured for dragging with fixed pixel margins.
     * Note: Dimensions (120x120) and Margins (5px) are used directly as specified.
     */
    private fun createDraggableLetter(letter: String): TextView {
        // Definimos el margen fijo en píxeles (5px)
        val marginInPixels = 5
        val marginDp = 1 // Distancia deseada en DP
        val marginPx = marginDp.dpToPx() // Conversión a Píxeles

        val textView = TextView(this).apply {
            text = letter
            textSize = 32f
            gravity = Gravity.CENTER
            setTextColor(Color.WHITE)
            setBackgroundResource(R.drawable.letter_bg)

            // Aplicamos el margen fijo de 5 píxeles, manteniendo las dimensiones fijas (120x120)
            // Usamos LinearLayout.LayoutParams para el manejo de márgenes en un LinearLayout/Flexbox
            layoutParams = LinearLayout.LayoutParams(150, 150).also {
                // Establece un margen de 5 píxeles a todos los lados
                it.setMargins(marginInPixels, marginInPixels, marginInPixels, marginInPixels)
            }
        }

        // Configure the listener to initiate the drag (Resto del código igual)
        textView.setOnLongClickListener { view ->
            val clipData = ClipData.newPlainText("letter", letter)
            val shadowBuilder = View.DragShadowBuilder(view)
            view.startDragAndDrop(clipData, shadowBuilder, view, 0)
            view.visibility = View.INVISIBLE
            true
        }
        return textView
    }

// Nota: La función dpToPx() y las llamadas a ella se han eliminado de esta versión
// para adherirse a la solicitud de usar píxeles fijos.

    /**
     * Checks if the letters in the answerZone match the target word.
     */
    private fun checkSpelling() {
        val spelledWord = StringBuilder()
        // Concatenate text from all children in the answerZone
        for (i in 0 until answerZone.childCount) {
            val child = answerZone.getChildAt(i)
            if (child is TextView) {
                spelledWord.append(child.text)
            }
        }

        val result = spelledWord.toString()

        // Comparison against the current target word
        if (result == currentWord) {
            Toast.makeText(this, "¡Correcto! Palabra: $currentWord", Toast.LENGTH_SHORT).show()
            goToNextWord()
        } else {
            Toast.makeText(this, "Incorrecto. Palabra formada: $result", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Advances to the next word/level.
     */
    private fun goToNextWord() {
        currentWordIndex++
        setupGame()
    }

    // Listener for drag events on both zones
    private val dragListener = View.OnDragListener { view, event ->
        if (view !is ViewGroup) { // Usamos ViewGroup para abarcar LinearLayout y Flexbox
            return@OnDragListener true
        }
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> {
                event.clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
            }
            // ... (ACTION_DRAG_ENTERED/EXITED/ENDED logic remains the same) ...
            DragEvent.ACTION_DROP -> {
                val originalView = event.localState as View
                val owner = originalView.parent as ViewGroup

                owner.removeView(originalView)
                val destination = view

                // Logic to insert the view at the approximate drop position (Improved UX)
                var insertIndex = 0
                for (i in 0 until destination.childCount) {
                    val child = destination.getChildAt(i)
                    // This logic works well for LinearLayout. Flexbox may behave differently
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
            // ... (Other drag events remain the same) ...
            else -> true
        }
    }
}