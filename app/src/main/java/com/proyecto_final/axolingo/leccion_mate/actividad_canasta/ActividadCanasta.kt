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
import androidx.lifecycle.lifecycleScope
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.leccion_mate.MenuLeccionMateActivity
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Actividad para el juego de "Canasta Matemática"
class ActividadCanasta : AppCompatActivity() {
    // Vistas del Juego
    private lateinit var gameView: CanastaGameView // Vista personalizada del juego
    private lateinit var tvExpression: TextView // Texto para mostrar la expresión matemática
    private lateinit var tvScore: TextView // Texto para mostrar la puntuación
    private lateinit var heart1: ImageView // Corazón 1 (vida)
    private lateinit var heart2: ImageView // Corazón 2 (vida)
    private lateinit var heart3: ImageView // Corazón 3 (vida)
    private lateinit var layoutGameContainer: ConstraintLayout // Contenedor del juego

    // Capas (Overlays)
    private lateinit var layoutStartOverlay: FrameLayout // Capa de inicio
    private lateinit var layoutValidation: LinearLayout // Capa de validación
    private lateinit var layoutFinalResult: LinearLayout // Capa de resultado final

    // Componentes de UI lógica
    private lateinit var btnStartGame: Button // Botón para iniciar el juego
    private lateinit var btnValidateResult: Button // Botón para validar el resultado
    private lateinit var btnReturnMenu: Button // Botón para regresar al menú
    private lateinit var tvFinalExpression: TextView // Texto con la expresión final
    private lateinit var etResultInput: EditText // Campo de entrada para el resultado
    private lateinit var tvCongratsTitle: TextView // Título de felicitaciones
    private lateinit var tvFinalScore: TextView // Texto con la puntuación final
    private lateinit var imgResultIcon: ImageView // Icono de resultado (éxito o error)

    // Estado del juego
    private var lives = 3 // Número de vidas
    private var score = 0 // Puntuación actual
    private val expression = mutableListOf<String>() // Expresión matemática en construcción

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actividad_mate_canasta)

        initializeViews() // Inicializar vistas
        setupListeners() // Configurar listeners

        // Estado inicial: Mostrar pantalla de inicio y ocultar el juego
        showStartScreen()
    }

    // Inicializa las vistas de la actividad
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

    // Configura los listeners para los botones y el juego
    private fun setupListeners() {
        // Listener del Juego (Canasta)
        gameView.setGameListener(object : CanastaGameView.GameListener {
            override fun onItemCaught(item: FallingItem) {
                runOnUiThread { handleItemCaught(item) } // Manejar ítem atrapado
            }

            override fun onLifeLost() {
                runOnUiThread { loseLife() } // Manejar pérdida de vida
            }

            override fun onGameOver() {
                runOnUiThread { showGameOverScreen() } // Mostrar pantalla de fin del juego
            }
        })

        // Botón para iniciar el juego
        btnStartGame.setOnClickListener {
            startGame()
        }

        // Botón para validar el resultado matemático
        btnValidateResult.setOnClickListener {
            validateUserMath()
        }

        // Botón para regresar al menú principal
        btnReturnMenu.setOnClickListener {
            val intent = Intent(this, MenuLeccionMateActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }
    }

    // --- GESTIÓN DE PANTALLAS ---

    // Muestra la pantalla de inicio
    private fun showStartScreen() {
        layoutStartOverlay.visibility = View.VISIBLE
        layoutGameContainer.visibility = View.VISIBLE // Mostrar fondo del juego
        layoutValidation.visibility = View.GONE
        layoutFinalResult.visibility = View.GONE

        // Asegurar que el juego no esté corriendo
        gameView.stopGame()
    }

    // Inicia el juego
    private fun startGame() {
        layoutStartOverlay.visibility = View.GONE
        layoutGameContainer.visibility = View.VISIBLE
        resetGameData() // Reiniciar datos del juego
        gameView.resetGame() // Iniciar el loop del juego
    }

    // Muestra la pantalla de validación de la ecuación
    private fun showValidationScreen() {
        gameView.stopGame()
        layoutGameContainer.visibility = View.GONE
        layoutValidation.visibility = View.VISIBLE

        tvFinalExpression.text = "${expression.joinToString(" ")} = ?"
        etResultInput.text.clear()
    }

    // Muestra la pantalla de victoria
    private fun showVictoryScreen() {
        layoutValidation.visibility = View.GONE
        layoutFinalResult.visibility = View.VISIBLE

        tvCongratsTitle.text = "¡FELICIDADES!"
        tvCongratsTitle.setTextColor(Color.parseColor("#4CAF50")) // Verde
        tvFinalScore.text = "Puntuación Final: $score"
        imgResultIcon.setImageResource(R.drawable.huevo_bien) // Icono de éxito
        saveScore()
    }

    // Muestra la pantalla de fin del juego
    private fun showGameOverScreen() {
        gameView.stopGame()
        layoutGameContainer.visibility = View.GONE
        layoutStartOverlay.visibility = View.GONE
        layoutValidation.visibility = View.GONE
        layoutFinalResult.visibility = View.VISIBLE

        tvCongratsTitle.text = "¡INTÉNTALO DE NUEVO!"
        tvCongratsTitle.setTextColor(Color.RED)
        tvFinalScore.text = "Te quedaste sin vidas\nPuntuación: $score"
        imgResultIcon.setImageResource(R.drawable.huevo) // Icono de error
        saveScore()
    }

    // Guarda la puntuación final en la base de datos
    private fun saveScore() {
        val finalScore = score.toFloat()
        val sessionManager = SessionManager(applicationContext)
        val userDao = AppDatabase.getDatabase(applicationContext, lifecycleScope).userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val username = sessionManager.loginFlow.first()
            if (username != null) {
                userDao.updateSCShapes(username, finalScore)
            }
        }
    }

    // --- LÓGICA DEL JUEGO ---

    // Maneja los ítems atrapados en el juego
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

    // Verifica si se puede agregar un número a la expresión
    private fun canAddNumber(): Boolean {
        return expression.isEmpty() || expression.last() in listOf("+", "-")
    }

    // Verifica si se puede agregar un signo a la expresión
    private fun canAddSign(): Boolean {
        return expression.isNotEmpty() && expression.last() !in listOf("+", "-")
    }

    // Verifica si la expresión matemática está completa
    private fun checkExpression() {
        val numbers = expression.filter { it !in listOf("+", "-") }.size
        val signs = expression.filter { it in listOf("+", "-") }.size

        if (numbers == 5 && signs == 4) {
            showValidationScreen()
        }
    }

    // Valida el resultado ingresado por el usuario
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
            Toast.makeText(this, "Incorrecto. La respuesta era $realResult", Toast.LENGTH_LONG).show()
            showGameOverScreen()
        }
    }

    // Calcula el resultado de la expresión matemática
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

    // Maneja la pérdida de una vida
    private fun loseLife() {
        lives--
        if (lives <= 0) {
            showGameOverScreen()
        }
        updateUI()
    }

    // Reinicia los datos del juego
    private fun resetGameData() {
        lives = 3
        score = 0
        expression.clear()
        updateUI()
    }

    // Actualiza la interfaz de usuario
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