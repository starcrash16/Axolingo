package com.proyecto_final.axolingo.leccion_mate.actividad_canasta

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.leccion_mate.MenuLeccionMateActivity

class ActividadCanasta : AppCompatActivity() {
    // Vistas del Juego
    private lateinit var gameView: CanastaGameView
    private lateinit var tvExpression: TextView
    private lateinit var tvScore: TextView
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView
    private lateinit var layoutGameContainer: ConstraintLayout

    // Capas (Overlays)
    private lateinit var layoutStartOverlay: FrameLayout
    private lateinit var layoutValidation: LinearLayout
    private lateinit var layoutFinalResult: LinearLayout

    // Componentes de UI lógica
    private lateinit var btnStartGame: Button
    private lateinit var btnValidateResult: Button
    private lateinit var btnReturnMenu: Button
    private lateinit var tvFinalExpression: TextView
    private lateinit var etResultInput: EditText
    private lateinit var tvCongratsTitle: TextView
    private lateinit var tvFinalScore: TextView
    private lateinit var imgResultIcon: ImageView

    // Estado del juego
    private var lives = 3
    private var score = 0
    private val expression = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_mate_canasta)

        initializeViews()
        setupListeners()

        // Estado inicial: Mostrar Popup de Inicio, Ocultar Juego
        showStartScreen()
    }

    private fun initializeViews() {
        // Juego
        gameView = findViewById(R.id.game_view)
        tvExpression = findViewById(R.id.tv_expression)
        tvScore = findViewById(R.id.tv_score)
        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
        layoutGameContainer = findViewById(R.id.layout_game_container)

        // Capas
        layoutStartOverlay = findViewById(R.id.layout_start_overlay)
        layoutValidation = findViewById(R.id.layout_validation)
        layoutFinalResult = findViewById(R.id.layout_final_result)

        // Botones y Textos
        btnStartGame = findViewById(R.id.btn_start_game)
        btnValidateResult = findViewById(R.id.btn_validate_result)
        btnReturnMenu = findViewById(R.id.btn_return_menu)
        tvFinalExpression = findViewById(R.id.tv_final_expression)
        etResultInput = findViewById(R.id.et_result_input)
        tvCongratsTitle = findViewById(R.id.tv_congrats_title)
        tvFinalScore = findViewById(R.id.tv_final_score)
        imgResultIcon = findViewById(R.id.img_result_icon)
    }

    private fun setupListeners() {
        // Listener del Juego (Canasta)
        gameView.setGameListener(object : CanastaGameView.GameListener {
            override fun onItemCaught(item: FallingItem) {
                runOnUiThread { handleItemCaught(item) }
            }

            override fun onLifeLost() {
                runOnUiThread { loseLife() }
            }

            override fun onGameOver() {
                runOnUiThread { showGameOverScreen() }
            }
        })

        // Botón Iniciar Juego (Popup inicial)
        btnStartGame.setOnClickListener {
            startGame()
        }

        // Botón Verificar Resultado (Pantalla de Ecuación)
        btnValidateResult.setOnClickListener {
            validateUserMath()
        }

        // Botón Regresar (Pantalla Final)
        btnReturnMenu.setOnClickListener {
            val intent = Intent(this, MenuLeccionMateActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    // --- GESTIÓN DE PANTALLAS ---

    private fun showStartScreen() {
        layoutStartOverlay.visibility = View.VISIBLE
        layoutGameContainer.visibility = View.VISIBLE // Se ve de fondo
        layoutValidation.visibility = View.GONE
        layoutFinalResult.visibility = View.GONE

        // Asegurar que el juego no corra
        gameView.stopGame()
    }

    private fun startGame() {
        layoutStartOverlay.visibility = View.GONE
        layoutGameContainer.visibility = View.VISIBLE
        resetGameData()
        gameView.resetGame() // Inicia el loop
    }

    private fun showValidationScreen() {
        gameView.stopGame()
        layoutGameContainer.visibility = View.GONE
        layoutValidation.visibility = View.VISIBLE

        tvFinalExpression.text = "${expression.joinToString(" ")} = ?"
        etResultInput.text.clear()
    }

    private fun showVictoryScreen() {
        layoutValidation.visibility = View.GONE
        layoutFinalResult.visibility = View.VISIBLE

        tvCongratsTitle.text = "¡FELICIDADES!"
        tvCongratsTitle.setTextColor(Color.parseColor("#4CAF50")) // Verde
        tvFinalScore.text = "Puntuación Final: $score"
        imgResultIcon.setImageResource(R.drawable.huevo_bien) // Icono de éxito
    }

    private fun showGameOverScreen() {
        gameView.stopGame()
        layoutGameContainer.visibility = View.GONE
        layoutStartOverlay.visibility = View.GONE
        layoutValidation.visibility = View.GONE
        layoutFinalResult.visibility = View.VISIBLE

        tvCongratsTitle.text = "¡INTÉNTALO DE NUEVO!"
        tvCongratsTitle.setTextColor(Color.RED)
        tvFinalScore.text = "Te quedaste sin vidas\nPuntuación: $score"
        imgResultIcon.setImageResource(R.drawable.huevo) // Icono de error (huevo malo)
    }

    // --- LÓGICA DEL JUEGO ---

    private fun handleItemCaught(item: FallingItem) {
        when (item.type) {
            ItemType.HUEVO_BUENO -> {
                score += 5
                updateUI()
            }
            ItemType.NUMERO -> {
                if (canAddNumber()) {
                    expression.add(item.value)
                    checkExpression()
                } else {
                    loseLife()
                }
            }
            ItemType.SIGNO -> {
                if (canAddSign()) {
                    expression.add(item.value)
                    checkExpression()
                } else {
                    loseLife()
                }
            }
            ItemType.HUEVO_MALO -> {
                loseLife()
            }
        }
        updateUI()
    }

    private fun canAddNumber(): Boolean {
        return expression.isEmpty() || expression.last() in listOf("+", "-")
    }

    private fun canAddSign(): Boolean {
        return expression.isNotEmpty() && expression.last() !in listOf("+", "-")
    }

    private fun checkExpression() {
        val numbers = expression.filter { it !in listOf("+", "-") }.size
        val signs = expression.filter { it in listOf("+", "-") }.size

        if (numbers == 5 && signs == 4) {
            showValidationScreen()
        }
    }

    private fun validateUserMath() {
        val inputStr = etResultInput.text.toString()
        if (inputStr.isEmpty()) {
            Toast.makeText(this, "Por favor escribe un resultado", Toast.LENGTH_SHORT).show()
            return
        }

        val userResult = inputStr.toIntOrNull()
        val realResult = calculateResult()

        if (userResult == realResult) {
            score += 20 // Bonus por acertar
            showVictoryScreen()
        } else {
            // Opción: Mostrar Game Over o dejar reintentar. 
            // Según tu prompt: "únicamente será valida si acierta". 
            // Si falla, podemos mandarlo a Game Over o mostrar mensaje.
            Toast.makeText(this, "Incorrecto. La respuesta era $realResult", Toast.LENGTH_LONG).show()
            showGameOverScreen()
        }
    }

    private fun calculateResult(): Int {
        if (expression.isEmpty()) return 0
        try {
            var result = expression[0].toInt()
            var i = 1
            while (i < expression.size) {
                val operator = expression[i]
                val operand = expression[i + 1].toInt()
                result = when (operator) {
                    "+" -> result + operand
                    "-" -> result - operand
                    else -> result
                }
                i += 2
            }
            return result
        } catch (e: Exception) {
            return 0
        }
    }

    private fun loseLife() {
        lives--
        if (lives <= 0) {
            showGameOverScreen()
        }
        updateUI()
    }

    private fun resetGameData() {
        lives = 3
        score = 0
        expression.clear()
        updateUI()
    }

    private fun updateUI() {
        tvScore.text = "Puntuación: $score"

        if (expression.isNotEmpty()) {
            tvExpression.text = expression.joinToString(" ")
        } else {
            tvExpression.text = "Atrapa un Número para empezar"
        }

        heart1.alpha = if (lives >= 1) 1f else 0.2f
        heart2.alpha = if (lives >= 2) 1f else 0.2f
        heart3.alpha = if (lives >= 3) 1f else 0.2f
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
    }

    override fun onResume() {
        super.onResume()
        gameView.resumeGame()
    }
}