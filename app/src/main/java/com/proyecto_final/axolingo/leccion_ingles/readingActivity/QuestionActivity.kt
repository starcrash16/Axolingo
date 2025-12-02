package com.proyecto_final.axolingo.leccion_ingles.readingActivity


import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import android.view.View
import android.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.gson.Gson // Necesario para deserializar la historia pasada
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.data.db.AppDatabase
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Actividad para manejar las preguntas de una historia en la lección de inglés
class QuestionActivity : BaseActivity() {

    // Elementos de la interfaz de usuario
    private lateinit var tvQuestion: TextView // Texto de la pregunta
    private var tvQuestionCounter: TextView? = null // Contador opcional "Pregunta X / Y"
    private lateinit var radioGroupOptions: RadioGroup // Grupo de opciones
    private lateinit var btnOption1: RadioButton // Opción 1
    private lateinit var btnOption2: RadioButton // Opción 2
    private lateinit var btnOption3: RadioButton // Opción 3
    private lateinit var btnOption4: RadioButton // Opción 4
    private lateinit var btnSubmitAnswer: Button // Botón para enviar la respuesta
    private lateinit var btnFeedback: Button // Botón de retroalimentación

    // Datos de la historia (pasados desde ReadingActivity)
    private lateinit var currentStory: Story // Historia actual
    private var currentQuestionIndex: Int = 0 // Índice de la pregunta actual
    private var score: Int = 0 // Puntuación del usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_layout)

        // Aplicar fondo sombreado
        window.setBackgroundDrawableResource(R.drawable.axo_biblioteca_shaded)

        // 1. Inicializar componentes de la interfaz de usuario
        tvQuestion = findViewById(R.id.tv_question)
        // Intenta obtener un TextView opcional para mostrar el contador de preguntas
        // El layout usa `tv_quiz_progress`, así que se busca ese id
        tvQuestionCounter = findViewById(R.id.tv_quiz_progress)
        radioGroupOptions = findViewById(R.id.radio_group_options)
        btnOption1 = findViewById(R.id.rb_option1)
        btnOption2 = findViewById(R.id.rb_option2)
        btnOption3 = findViewById(R.id.rb_option3)
        btnOption4 = findViewById(R.id.rb_option4)
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer)
        btnFeedback = findViewById(R.id.btn_feedback)

        // 2. Obtener los datos de la historia pasados desde ReadingActivity
        val storyJson = intent.getStringExtra("story_data")
        if (storyJson != null) {
            currentStory = Gson().fromJson(storyJson, Story::class.java)
            displayQuestion() // Comenzar a mostrar preguntas
        } else {
            Toast.makeText(this, "Error: No story data found!", Toast.LENGTH_LONG).show()
            finish() // Cerrar la actividad si no hay datos
        }

        // 3. Configurar el botón para enviar respuestas
        btnSubmitAnswer.setOnClickListener {
            checkAnswer()
        }

        // Configurar el botón de retroalimentación
        btnFeedback.setOnClickListener {
            showFeedbackDialog()
        }
    }

    // Mostrar la pregunta actual en la interfaz
    private fun displayQuestion() {
        // Actualiza contador (si existe)
        updateQuestionCounter()

        if (currentQuestionIndex < currentStory.questions.size) {
            val question = currentStory.questions[currentQuestionIndex]
            tvQuestion.text = question.question

            // Limpiar cualquier selección previa
            radioGroupOptions.clearCheck()

            // Establecer el texto de las opciones
            btnOption1.text = question.options[0]
            btnOption2.text = question.options[1]
            btnOption3.text = question.options[2]
            btnOption4.text = question.options[3]

            // Asegurarse de que los botones estén habilitados
            btnOption1.isEnabled = true
            btnOption2.isEnabled = true
            btnOption3.isEnabled = true
            btnOption4.isEnabled = true

        } else {
            // Todas las preguntas respondidas
            showFinalScore()
        }
    }

    // Actualiza un TextView opcional con el formato "Pregunta X / Y"
    private fun updateQuestionCounter() {
        val total = currentStory.questions.size
        val indexToShow = (currentQuestionIndex + 1).coerceAtMost(total)
        tvQuestionCounter?.text = "Pregunta $indexToShow / $total"
    }

    // Verificar la respuesta seleccionada por el usuario
    private fun checkAnswer() {
        val selectedOptionId = radioGroupOptions.checkedRadioButtonId
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show()
            return // No continuar si no se seleccionó una opción
        }

        val selectedRadioButton: RadioButton = findViewById(selectedOptionId)
        val selectedAnswer = selectedRadioButton.text.toString()

        // Seguridad: obtener la respuesta correcta de forma segura
        val correctAnswer = currentStory.questions.getOrNull(currentQuestionIndex)?.answer_c
        if (correctAnswer == null) {
            // Si por alguna razón no existe la pregunta actual, finalizamos
            showFinalScore()
            return
        }

        if (selectedAnswer == correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect. The answer was: $correctAnswer", Toast.LENGTH_LONG).show()
        }

        // Incrementa el índice de la pregunta y muestra la siguiente
        currentQuestionIndex += 1
        displayQuestion() // Pasar a la siguiente pregunta o finalizar
    }

    // Mostrar la puntuación final del usuario
    private fun showFinalScore() {
        val finalScore = score.toFloat() / 3.0f
        val sessionManager = SessionManager(applicationContext)
        val userDao = AppDatabase.getDatabase(applicationContext).userDao()

        lifecycleScope.launch(Dispatchers.IO) {
            val username = sessionManager.loginFlow.first()
            if (username != null) {
                userDao.updateSCReading(username, finalScore)
            }
        }
        tvQuestion.text = "Quiz Finished! Your score is: $score / ${currentStory.questions.size}"
        radioGroupOptions.visibility = RadioGroup.GONE // Ocultar opciones
        btnSubmitAnswer.text = "Back to Menu"
        btnFeedback.visibility = View.VISIBLE // Mostrar botón de retroalimentación
        btnSubmitAnswer.setOnClickListener {
            finish() // O navegar a una pantalla de resultados/menú principal
        }
        Toast.makeText(this, "Quiz completed!", Toast.LENGTH_LONG).show()

        
    }

    // Mostrar un diálogo con las respuestas correctas
    private fun showFeedbackDialog() {
        val builder = StringBuilder()
        for ((index, question) in currentStory.questions.withIndex()) {
            builder.append("${index + 1}. ${question.question}\n")
            builder.append("R = ${question.answer_c}\n\n")
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