package com.proyecto_final.axolingo.leccion_ingles

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.leccion_ingles.leccion_ingles.LineDrawingView
import java.util.Collections
import kotlin.math.abs

// Data class to mirror the JSON structure for a single item
data class VocabItem(val word: String, val definition: String)

class VocabularyActivity : BaseActivity() {

    private lateinit var lineDrawingView: LineDrawingView
    private lateinit var tvCompletionMessage: TextView
    private lateinit var leftContainer: LinearLayout
    private lateinit var rightContainer: LinearLayout

    private lateinit var allVocab: List<VocabItem>
    private lateinit var wordButtons: List<Button>
    private lateinit var definitionButtons: List<Button>

    // State for matching logic
    private var startButton: Button? = null
    private var matchesCount = 0
    private val correctMatches = mutableMapOf<String, String>() // Word -> Definition
    private val currentMatches = mutableMapOf<Button, Button>() // Button -> Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary)

        // Initialize UI and data
        lineDrawingView = findViewById(R.id.line_drawing_view)
        tvCompletionMessage = findViewById(R.id.tv_completion_message)
        leftContainer = findViewById(R.id.left_container)
        rightContainer = findViewById(R.id.right_container)

        loadAndSetupGame()

        // Set up the touch interceptor on the drawing view
        lineDrawingView.setOnTouchListener(::handleTouch)
    }

    // --- GAME SETUP ---

    private fun loadAndSetupGame() {
        val jsonString = VOCAB_JSON // Load JSON data
        val gson = Gson()
        val type = object : TypeToken<List<VocabItem>>() {}.type
        allVocab = gson.fromJson(jsonString, type)

        // Shuffle definitions for the right column
        val definitions = allVocab.map { it.definition }.toMutableList()
        Collections.shuffle(definitions)

        // 1. Store correct pairs (Word -> Definition)
        allVocab.forEach { item ->
            correctMatches[item.word] = item.definition
        }

        // 2. Setup Buttons
        wordButtons = setupButtons(leftContainer, allVocab.map { it.word }, true)
        definitionButtons = setupButtons(rightContainer, definitions, false)

        // 3. Attach click listeners for initial tap
        (wordButtons + definitionButtons).forEach { button ->
            button.setOnClickListener { onButtonTapped(it as Button) }
        }
    }

    // Helper to setup buttons in containers
    private fun setupButtons(container: LinearLayout, texts: List<String>, isWord: Boolean): List<Button> {
        val buttons = mutableListOf<Button>()
        // Note: Assumes the container is already populated with 6 placeholder buttons from XML
        for (i in 0 until 6) {
            val button = container.getChildAt(i) as Button
            button.text = texts[i]
            // Tag the button to identify if it's a 'word' or 'definition'
            button.tag = if (isWord) "word" else "definition"
            buttons.add(button)
        }
        return buttons
    }

    // --- GAME LOGIC ---

    private fun onButtonTapped(button: Button) {
        if (!button.isEnabled) return

        if (startButton == null) {
            // First tap: Select starting point
            startButton = button
            button.setBackgroundColor(Color.YELLOW)
        } else if (startButton == button) {
            // Tapped same button: Deselect
            resetSelection(button)
            lineDrawingView.clearTempLine()
        } else {
            // Second tap: Attempt to match
            val endButton = button
            attemptMatch(startButton!!, endButton)
        }
    }

    private fun attemptMatch(start: Button, end: Button) {
        // Validate that one is a word and one is a definition
        val isWord = start.tag == "word" && end.tag == "definition"
        val isDefinition = start.tag == "definition" && end.tag == "word"

        if (isWord || isDefinition) {
            val wordButton = if (isWord) start else end
            val definitionButton = if (isWord) end else start

            val wordText = wordButton.text.toString()
            val definitionText = definitionButton.text.toString()

            if (correctMatches[wordText] == definitionText) {
                // CORRECT MATCH!
                matchesCount++

                // 1. Draw the line permanently
                val startPoint = getButtonCenter(wordButton)
                val endPoint = getButtonCenter(definitionButton)
                lineDrawingView.addMatchedLine(startPoint, endPoint, Color.parseColor("#4CAF50")) // Green line

                // 2. Disable/Style buttons
                wordButton.isEnabled = false
                definitionButton.isEnabled = false
                resetSelection(wordButton)

                Toast.makeText(this, "Correct Match!", Toast.LENGTH_SHORT).show()

                if (matchesCount == allVocab.size) {
                    onGameComplete()
                }

            } else {
                // INCORRECT MATCH!
                Toast.makeText(this, "Incorrect Match. Try again.", Toast.LENGTH_SHORT).show()
                lineDrawingView.clearTempLine()
            }
        } else {
            // Invalid combination (Word-Word or Def-Def)
            Toast.makeText(this, "Must match a Word with a Definition.", Toast.LENGTH_SHORT).show()
            lineDrawingView.clearTempLine()
        }

        resetSelection(start) // Reset the initial selection
    }

    private fun resetSelection(button: Button?) {
        // Reset background color (uses the default selector from XML)
        button?.setBackgroundResource(R.drawable.matching_button_bg)
        startButton = null
        lineDrawingView.clearTempLine()
    }

    private fun onGameComplete() {
        tvCompletionMessage.visibility = View.VISIBLE
        Toast.makeText(this, "Congratulations! All pairs matched!", Toast.LENGTH_LONG).show()
        // Here you would add navigation back to the main menu or next level
    }

    // --- TOUCH HANDLING (for drawing the temporary line) ---

    private fun handleTouch(v: View, event: MotionEvent): Boolean {
        if (startButton == null) {
            // No button selected, let the button click handle the tap
            return false
        }

        // Get the coordinates of the touch relative to the LineDrawingView
        val currentTouchPoint = PointF(event.x, event.y)
        val startCenter = getButtonCenter(startButton!!)

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                // Update the temporary line end point
                lineDrawingView.tempLineStart = startCenter
                lineDrawingView.tempLineEnd = currentTouchPoint
                lineDrawingView.invalidate()
            }
            MotionEvent.ACTION_UP -> {
                // Check if the release point is over another *enabled* button
                val releasedButton = findButtonAtPoint(currentTouchPoint)

                if (releasedButton != null && releasedButton != startButton && releasedButton.isEnabled) {
                    // Successful release over a button: attempt match via tap logic
                    attemptMatch(startButton!!, releasedButton)
                } else {
                    // Release failed/outside a valid button: clear temporary line
                    resetSelection(startButton)
                }
            }
        }
        return true
    }

    // --- UTILITY FUNCTIONS ---

    // Finds the center coordinates of a button relative to the LineDrawingView
    private fun getButtonCenter(button: Button): PointF {
        val location = IntArray(2)
        button.getLocationOnScreen(location)

        val containerLocation = IntArray(2)
        lineDrawingView.getLocationOnScreen(containerLocation)

        val x = location[0] + button.width / 2f - containerLocation[0]
        val y = location[1] + button.height / 2f - containerLocation[1]
        return PointF(x, y)
    }

    // Finds which button (if any) is under the touch point
    private fun findButtonAtPoint(point: PointF): Button? {
        val allButtons = wordButtons + definitionButtons

        for (button in allButtons) {
            val location = IntArray(2)
            button.getLocationOnScreen(location)

            val containerLocation = IntArray(2)
            lineDrawingView.getLocationOnScreen(containerLocation)

            val left = location[0] - containerLocation[0]
            val top = location[1] - containerLocation[1]
            val right = left + button.width
            val bottom = top + button.height

            // Check if the point is within the button's bounds
            if (point.x >= left && point.x <= right && point.y >= top && point.y <= bottom) {
                return button
            }
        }
        return null
    }

    // The JSON data, included here for simplicity in this file
    private val VOCAB_JSON = """
[
  {
    "word": "Swim",
    "definition": "To move through water by moving your body."
  },
  {
    "word": "Kelp",
    "definition": "A large, brown seaweed that grows in cool water."
  },
  {
    "word": "Muncher",
    "definition": "A person or thing that chews loudly or steadily."
  },
  {
    "word": "Goal",
    "definition": "The object or place where players try to send a ball in sports like soccer."
  },
  {
    "word": "Shovel",
    "definition": "A tool with a broad scoop used for lifting and moving materials like dirt."
  },
  {
    "word": "Marble",
    "definition": "A small, round ball of glass."
  }
]
"""
}