package com.proyecto_final.axolingo.leccion_ingles.readingActivity


import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.RadioGroup
import android.widget.RadioButton
import android.widget.Toast
import com.google.gson.Gson // Necesario para deserializar la historia pasada
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R

class QuestionActivity : BaseActivity() {

    // UI elements
    private lateinit var tvQuestion: TextView
    private lateinit var radioGroupOptions: RadioGroup
    private lateinit var btnOption1: RadioButton
    private lateinit var btnOption2: RadioButton
    private lateinit var btnOption3: RadioButton
    private lateinit var btnOption4: RadioButton
    private lateinit var btnSubmitAnswer: Button // Botón para enviar la respuesta

    // Story data (will be passed from ReadingActivity)
    private lateinit var currentStory: Story
    private var currentQuestionIndex: Int = 0
    private var score: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.question_layout)

        // Apply shaded background
        window.setBackgroundDrawableResource(R.drawable.axo_biblioteca_shaded)

        // 1. Initialize UI components
        tvQuestion = findViewById(R.id.tv_question)
        radioGroupOptions = findViewById(R.id.radio_group_options)
        btnOption1 = findViewById(R.id.rb_option1)
        btnOption2 = findViewById(R.id.rb_option2)
        btnOption3 = findViewById(R.id.rb_option3)
        btnOption4 = findViewById(R.id.rb_option4)
        btnSubmitAnswer = findViewById(R.id.btn_submit_answer)

        // 2. Get the story data passed from ReadingActivity
        val storyJson = intent.getStringExtra("story_data")
        if (storyJson != null) {
            currentStory = Gson().fromJson(storyJson, Story::class.java)
            displayQuestion() // Start displaying questions
        } else {
            Toast.makeText(this, "Error: No story data found!", Toast.LENGTH_LONG).show()
            finish() // Close the activity if no data
        }

        // 3. Set up submit button click listener
        btnSubmitAnswer.setOnClickListener {
            checkAnswer()
        }
    }

    private fun displayQuestion() {
        if (currentQuestionIndex < currentStory.questions.size) {
            val question = currentStory.questions[currentQuestionIndex]
            tvQuestion.text = question.question

            // Clear any previous selection
            radioGroupOptions.clearCheck()

            // Set options text
            btnOption1.text = question.options[0]
            btnOption2.text = question.options[1]
            btnOption3.text = question.options[2]
            btnOption4.text = question.options[3]

            // Ensure radio buttons are enabled
            btnOption1.isEnabled = true
            btnOption2.isEnabled = true
            btnOption3.isEnabled = true
            btnOption4.isEnabled = true

        } else {
            // All questions answered
            showFinalScore()
        }
    }

    private fun checkAnswer() {
        val selectedOptionId = radioGroupOptions.checkedRadioButtonId
        if (selectedOptionId == -1) {
            Toast.makeText(this, "Please select an option!", Toast.LENGTH_SHORT).show()
            return // Don't proceed if no option is selected
        }

        val selectedRadioButton: RadioButton = findViewById(selectedOptionId)
        val selectedAnswer = selectedRadioButton.text.toString()

        val correctAnswer = currentStory.questions[currentQuestionIndex].answer_c

        if (selectedAnswer == correctAnswer) {
            score++
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Incorrect. The answer was: $correctAnswer", Toast.LENGTH_LONG).show()
        }

        currentQuestionIndex++
        displayQuestion() // Move to the next question or finish
    }

    private fun showFinalScore() {
        tvQuestion.text = "Quiz Finished! Your score is: $score / ${currentStory.questions.size}"
        radioGroupOptions.visibility = RadioGroup.GONE // Hide options
        btnSubmitAnswer.text = "Back to Menu"
        btnSubmitAnswer.setOnClickListener {
            finish() // Or navigate to a results screen/main menu
        }
        Toast.makeText(this, "Quiz completed!", Toast.LENGTH_LONG).show()
    }
}