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
    private lateinit var progressBar: ProgressBar
    private lateinit var tvGameFinished: TextView
    private lateinit var btnBackToMenu: Button

    // Game state variables
    private var results: MutableList<Boolean> = mutableListOf()
    private val gameWords = listOf("AXO", "CAT", "DOG", "SUN")
    private var currentWordIndex: Int = 0
    private val currentWord: String
        get() = if (gameWords.isNotEmpty() && currentWordIndex < gameWords.size) gameWords[currentWordIndex] else ""

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
        progressBar = findViewById(R.id.progressBar)
        tvGameFinished = findViewById(R.id.tvGameFinished)
        btnBackToMenu = findViewById(R.id.btnBackToMenu)

        // Apply drag listeners
        answerZone.setOnDragListener(dragListener)
        sourceZone.setOnDragListener(dragListener)

        // Set up button listeners
        btnValidate.setOnClickListener { onValidateClicked() }
        findViewById<Button>(R.id.btnRestart).setOnClickListener { resetCurrentWord() }
        btnBackToMenu.setOnClickListener {
            val intent = Intent(this, MenuLeccionInglesActivity::class.java)
            startActivity(intent)
            finish()
        }

        setupGame()
    }

    /**
     * Sets up the current level/word, generating the expanded letter bank.
     */
    private fun setupGame() {
        if (currentWordIndex >= gameWords.size) {
            showGameFinished()
            return
        }

        // Update the progress counter
        progressBar.max = gameWords.size
        progressBar.progress = currentWordIndex
        wordToSpellText.text = "Deletrea: '${currentWord}'"

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
        val marginInPixels = 5
        val marginDp = 1
        val marginPx = marginDp.dpToPx()

        val warmColors = listOf(
            Color.parseColor("#FFB74D"), // Orange
            Color.parseColor("#FFD54F"), // Yellow
            Color.parseColor("#FF8A65"), // Light Red
            Color.parseColor("#4FC3F7"), // Light Blue
            Color.parseColor("#81C784"), // Light Green
            Color.parseColor("#BA68C8"), // Purple
            Color.parseColor("#F06292"), // Pink
            Color.parseColor("#FFF176"), // Lemon
            Color.parseColor("#A1887F"), // Brown
            Color.parseColor("#E57373")  // Red
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
    private fun onValidateClicked() {
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
        results.add(result == currentWord)
        currentWordIndex++
        setupGame()
    }

    private fun resetCurrentWord() {
        // Move all letters from answerZone back to sourceZone
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

    private fun showGameFinished() {
        // Hide all game views
        progressBar.visibility = View.GONE
        wordToSpellText.visibility = View.GONE
        answerZone.visibility = View.GONE
        sourceZone.visibility = View.GONE
        findViewById<Button>(R.id.btnValidate).visibility = View.GONE
        findViewById<Button>(R.id.btnRestart).visibility = View.GONE
        findViewById<View>(R.id.dottedLine).visibility = View.GONE
        // Show final message and back button
        val score = results.count { it }
        tvGameFinished.text = "¡Felicidades, Juego Terminado!\nAciertos: $score/${gameWords.size}"
        tvGameFinished.visibility = View.VISIBLE
        btnBackToMenu.visibility = View.VISIBLE
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