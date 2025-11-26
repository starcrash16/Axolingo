package com.proyecto_final.axolingo.leccion_ingles.vocabActividad

import android.graphics.Color
import android.graphics.PointF
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.proyecto_final.axolingo.R
import java.io.InputStreamReader
import kotlin.random.Random

class VocabActivity : AppCompatActivity() {
    private lateinit var leftContainer: LinearLayout
    private lateinit var rightContainer: LinearLayout
    private lateinit var drawingView: com.proyecto_final.axolingo.leccion_ingles.leccion_ingles.LineDrawingView
    private lateinit var btnValidate: Button
    private lateinit var btnReset: Button

    private val colorList = arrayListOf(
        Color.parseColor("#4FC3F7"), // Blue
        Color.parseColor("#81C784"), // Green
        Color.parseColor("#FFB74D"), // Orange
        Color.parseColor("#BA68C8"), // Purple
        Color.parseColor("#F06292"), // Pink
        Color.parseColor("#FFD54F")  // Yellow
    )
    private val usedColors = mutableSetOf<Int>()
    private val connections = mutableListOf<Connection>()
    private var currentConnection: Connection? = null
    private var words: List<String> = emptyList()
    private var definitions: List<String> = emptyList()
    private var correctPairs: Map<String, String> = emptyMap()

    data class Connection(val wordBtn: TextView, val defBtn: TextView?, val color: Int, val start: PointF, val end: PointF)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vocabulary)
        leftContainer = findViewById(R.id.left_container)
        rightContainer = findViewById(R.id.right_container)
        drawingView = findViewById(R.id.line_drawing_view)
        btnValidate = findViewById(R.id.btnValidate)
        btnReset = findViewById(R.id.btnReset)
        drawingView = findViewById(R.id.line_drawing_view)
        loadWordsAndDefinitions()
        setupGame()
        btnValidate.setOnClickListener { validateConnections() }
        btnReset.setOnClickListener { resetGame() }
    }

    private fun loadWordsAndDefinitions() {
        val inputStream = resources.openRawResource(R.raw.vocab)
        val jsonString = inputStream.reader().readText()
        val jsonObj = Gson().fromJson(jsonString, VocabJson::class.java)
        val allPairs = jsonObj.words.shuffled().take(6)
        words = allPairs.map { it.word }
        definitions = allPairs.map { it.definition }.shuffled()
        correctPairs = allPairs.associate { it.word to it.definition }
    }

    data class VocabJson(val words: List<VocabItem>)
    data class VocabItem(val word: String, val definition: String)

    private fun setupGame() {
        drawingView.clearAllLines()
        leftContainer.removeAllViews()
        rightContainer.removeAllViews()
        drawingView.clearAllLines()
        connections.clear()
        usedColors.clear()
        for (word in words) {
            val tv = createWordBox(word)
            leftContainer.addView(tv)
        }
        for (def in definitions) {
            val tv = createDefBox(def)
            rightContainer.addView(tv)
        }
    }

    private fun createWordBox(word: String): TextView {
        val tv = TextView(this)
        tv.text = word
        tv.setBackgroundResource(R.drawable.vocab_item_bg)
        tv.setTextColor(Color.BLACK)
        tv.textSize = 16f
        tv.setPadding(24, 20, 24, 20) // Padding interior notable
        tv.gravity = android.view.Gravity.CENTER // Texto centrado
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 32, 8, 32) // Aumentar separación vertical
        tv.layoutParams = params
        tv.setOnTouchListener { v, event -> handleWordTouch(tv, event) }
        return tv
    }

    private fun createDefBox(def: String): TextView {
        val tv = TextView(this)
        tv.text = def
        tv.setBackgroundResource(R.drawable.vocab_item_bg)
        tv.setTextColor(Color.BLACK)
        tv.textSize = 16f
        tv.setPadding(24, 20, 24, 20) // Padding interior notable
        tv.gravity = android.view.Gravity.CENTER // Texto centrado y justificado
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(8, 32, 8, 32) // Aumentar separación vertical
        tv.layoutParams = params
        return tv
    }

    private fun handleWordTouch(wordBox: TextView, event: MotionEvent): Boolean {
        if (connections.any { it.wordBtn == wordBox }) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                val color = getUniqueColor()
                wordBox.setBackgroundColor(color)
                val start = getCenter(wordBox) // Punto central de la caja
                currentConnection = Connection(wordBox, null, color, start, start)
                drawingView.setDynamicLine(start, start, color)
            }
            MotionEvent.ACTION_MOVE -> {
                currentConnection?.let {
                    drawingView.setDynamicLine(it.start, PointF(event.rawX, event.rawY), it.color)
                }
            }
            MotionEvent.ACTION_UP -> {
                val defBox = findDefBoxAt(event.rawX, event.rawY)
                if (defBox != null && connections.none { it.defBtn == defBox }) {
                    // Conexión exitosa
                    defBox.setBackgroundColor(currentConnection!!.color)
                    val end = getCenter(defBox) // Punto central de la caja de definición
                    drawingView.addPermanentLine(currentConnection!!.start, end, currentConnection!!.color)
                    connections.add(currentConnection!!.copy(defBtn = defBox, end = end))
                } else {
                    // Conexión fallida: restaurar el fondo de la palabra
                    wordBox.setBackgroundResource(R.drawable.vocab_item_bg)
                    usedColors.remove(currentConnection!!.color)
                }
                drawingView.clearDynamicLine()
                currentConnection = null
            }
        }
        return true
    }

    private fun getUniqueColor(): Int {
        val available = colorList.filter { !usedColors.contains(it) }
        val color = if (available.isNotEmpty()) available.random() else colorList.random()
        usedColors.add(color)
        return color
    }

    private fun getCenter(view: View): PointF {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        val x = loc[0] + view.width / 2f
        val y = loc[1] + view.height / 2f
        return PointF(x, y-60)
    }

    private fun getRightCenter(view: View): PointF {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        val x = loc[0] + view.width.toFloat() // Borde derecho
        val y = loc[1] + view.height / 2f // Centro vertical
        return PointF(x, y-60)
    }

    private fun getLeftCenter(view: View): PointF {
        val loc = IntArray(2)
        view.getLocationOnScreen(loc)
        val x = loc[0].toFloat() // Borde izquierdo
        val y = loc[1] + view.height / 2f // Centro vertical
        return PointF(x, y-60)
    }

    private fun findDefBoxAt(x: Float, y: Float): TextView? {
        for (i in 0 until rightContainer.childCount) {
            val tv = rightContainer.getChildAt(i) as TextView
            val loc = IntArray(2)
            tv.getLocationOnScreen(loc)
            val left = loc[0].toFloat()
            val top = loc[1].toFloat()
            val right = left + tv.width
            val bottom = top + tv.height
            if (x >= left && x <= right && y >= top && y <= bottom) return tv
        }
        return null
    }

    private fun validateConnections() {
        val correct = connections.count { conn ->
            conn.defBtn?.text == correctPairs[conn.wordBtn.text]
        }

        // Limpiar la pantalla y mostrar el mensaje de felicitación
        leftContainer.visibility = View.GONE
        rightContainer.visibility = View.GONE
        btnValidate.visibility = View.GONE
        btnReset.visibility = View.GONE
        drawingView.clearAllLines()

        val congratulationsContainer = findViewById<LinearLayout>(R.id.congratulations_container)
        val tvScore = findViewById<TextView>(R.id.tv_score)
        congratulationsContainer.visibility = View.VISIBLE
        tvScore.text = "¡Felicidades! Tuviste $correct aciertos de 6."

        // Configurar botones
        val btnMainMenu = findViewById<com.proyecto_final.axolingo.art.botons.BotonMenuPrincipal>(R.id.btn_main_menu)
        val btnRestartGame = findViewById<com.proyecto_final.axolingo.art.botons.BotonMenuPrincipalAzul>(R.id.btn_restart_game)

        btnMainMenu.setOnClickListener {
            finish() // Regresar al menú principal
        }

        btnRestartGame.setOnClickListener {
            congratulationsContainer.visibility = View.GONE
            leftContainer.visibility = View.VISIBLE
            rightContainer.visibility = View.VISIBLE
            btnValidate.visibility = View.VISIBLE
            btnReset.visibility = View.VISIBLE
            resetGame()
        }
    }

    private fun resetGame() {
        loadWordsAndDefinitions()
        setupGame()
    }
}