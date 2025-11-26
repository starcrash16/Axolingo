// File: ReadingActivity.kt

package com.proyecto_final.axolingo.leccion_ingles.readingActivity

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.proyecto_final.axolingo.BaseActivity
import com.proyecto_final.axolingo.R
import com.proyecto_final.axolingo.art.lectura_Art.StageProgressBar
import java.util.Random
import android.content.Intent

// --- MAPPING CONSTANTS ---
// We map the JSON field names to their index for sequential reading.
private const val STAGE_BEGINNING = 0
private const val STAGE_DEV_1 = 1
private const val STAGE_DEV_2 = 2
private const val STAGE_END = 3
private const val TOTAL_STAGES = 4

class ReadingActivity : BaseActivity() {

    // UI elements (lateinit for setup in onCreate)
    private lateinit var tvTitle: TextView
    private lateinit var ivStoryImage: ImageView
    private lateinit var tvDescription: TextView
    private lateinit var btnNext: Button
    private lateinit var stageProgressBar: StageProgressBar

    // Story state variables
    private lateinit var allStories: List<Story>
    private lateinit var currentStory: Story
    private var currentStage: Int = STAGE_BEGINNING

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Note: Assumes the layout file is named readinglayout.xml
        setContentView(R.layout.readinglayout)

        // 1. Initialize UI components
        tvTitle = findViewById(R.id.tv_story_title)
        ivStoryImage = findViewById(R.id.iv_story_image)
        tvDescription = findViewById(R.id.tv_story_description)
        btnNext = findViewById(R.id.btn_next_stage)
        stageProgressBar = findViewById(R.id.story_progress_bar)
        stageProgressBar.setTotalStages(TOTAL_STAGES)
        stageProgressBar.setStage(currentStage)

        // 2. Load and select story randomly
        loadAndSelectRandomStory()

        // 3. Set up the button click listener
        btnNext.setOnClickListener {
            advanceStory()
        }
    }

    // Loads the JSON string, parses it, and selects one story randomly
    private fun loadAndSelectRandomStory() {
        // In a real app, this JSON string would be loaded from a file or network.
        val jsonString = JSON_STORIES

        // Use Gson to parse the array of Story objects
        val gson = Gson()
        // Type token to help Gson parse a List<Story> correctly
        val type = TypeToken.getParameterized(List::class.java, Story::class.java).type
        allStories = gson.fromJson(jsonString, type)

        // Select a random story
        val randomIndex = Random().nextInt(allStories.size)
        currentStory = allStories[randomIndex]

        // Start the reading session
        currentStage = STAGE_BEGINNING
        stageProgressBar.setStage(currentStage)
        updateUIForCurrentStage()
    }

    // Advances the story stage or finishes the reading
    private fun advanceStory() {
        currentStage++

        if (currentStage < TOTAL_STAGES) {
            // La historia continúa, actualiza la pantalla
            updateUIForCurrentStage()
        } else {
            // La historia ha terminado. Prepara la transición al cuestionario.

          stageProgressBar.setStage(TOTAL_STAGES)

            btnNext.text = "Start Quiz" // Cambia el texto del botón
            tvDescription.text = "You finished the story! Tap the button to start the quiz."

            // 1. Serializa el objeto 'currentStory' a una cadena JSON
            val storyJson = Gson().toJson(currentStory)

            // 2. Crea el Intent para iniciar QuestionActivity
            val intent = Intent(this, QuestionActivity::class.java)

            // 3. Adjunta la cadena JSON al Intent para que QuestionActivity pueda acceder a los datos
            intent.putExtra("story_data", storyJson)

            // 4. Inicia la nueva Activity
            startActivity(intent)

            // 5. Opcional: Finaliza ReadingActivity para que el usuario no pueda volver con el botón 'Back'
            finish()
        }
    }

    // Updates the screen based on the current reading stage
    private fun updateUIForCurrentStage() {
        tvTitle.text = currentStory.title
        btnNext.text = "Continue Reading"
      stageProgressBar.setStage(currentStage)

        // Get text and image key based on the current stage index
        val (stageText, imageKey) = when (currentStage) {
            STAGE_BEGINNING -> Pair(currentStory.beginning, currentStory.image_keys[STAGE_BEGINNING])
            STAGE_DEV_1 -> Pair(currentStory.development_1, currentStory.image_keys[STAGE_DEV_1])
            STAGE_DEV_2 -> Pair(currentStory.development_2, currentStory.image_keys[STAGE_DEV_2])
            STAGE_END -> Pair(currentStory.end, currentStory.image_keys[STAGE_END])
            else -> Pair("", "") // Should not happen
        }

        // 1. Update text description
        tvDescription.text = stageText

        // 2. Update image based on the key
        val resourceId = getResourceId(imageKey)
        if (resourceId != 0) {
            ivStoryImage.setImageResource(resourceId)
        } else {
            ivStoryImage.setImageResource(R.drawable.placeholder_default) // Fallback image
        }
    }

    // Helper function to dynamically get the resource ID from the string name
    private fun getResourceId(resourceName: String): Int {
        // Looks up the drawable ID using the string name (e.g., "axolotl_football_field_start")
        return resources.getIdentifier(
            resourceName.toLowerCase(), // Ensure resource names are lowercase in drawable folder
            "drawable",
            packageName
        )
    }

}


// --- CONSTANT JSON STRING ---
private const val JSON_STORIES = """
[
  {
    "id": 1,
    "title": "Juanito's Big Kick",
    "beginning": "Juanito is a small axolotl. He plays soccer today. His team is called 'The Pink Fins.' The other team is 'The Muddy Munchers.'",
    "development_1": "The score is tied. The ball is near the big weeds. Paco tries to get the ball. A Muddy Muncher stops Paco.",
    "development_2": "Juanito swims very fast. He sees the ball. He pushes the ball with his pink face. The ball goes into the net! Juanito scores the goal.",
    "end": "The game is over. The Pink Fins win! Juanito is very happy. His mom and dad cheer loudly. They tell him, 'Good job, Juanito!'",
    "image_keys": [
      "axolotl_football_field_start",
      "axolotl_paco_pass_block",
      "axolotl_juanito_scores_face_goal",
      "axolotl_family_celebration_win"
    ],
    "questions": [
      {
        "question": "What is Juanito's team name?",
        "options": [
          "The Muddy Munchers",
          "The Green Scales",
          "The Pink Fins",
          "The Fast Swimmers"
        ],
        "answer_c": "The Pink Fins"
      },
      {
        "question": "What part of his body did Juanito use to score?",
        "options": [
          "His tail",
          "His fin",
          "His face",
          "His hand"
        ],
        "answer_c": "His face"
      },
      {
        "question": "Who wins the soccer game?",
        "options": [
          "The Muddy Munchers",
          "Paco's team",
          "The Pink Fins",
          "Nobody"
        ],
        "answer_c": "The Pink Fins"
      }
    ]
  },
  {
    "id": 2,
    "title": "Lola and the Flowerpot",
    "beginning": "Paco and his sister, Lola, play a game. They play hide-and-seek. Lola hides inside a flowerpot. This pot belongs to Mama Axolotl.",
    "development_1": "Paco finds the pot. 'I see you, Lola!' he says. Lola does not want to come out. She says, 'I am not here!'",
    "development_2": "Paco pulls the pot. Lola holds on tight. CRASH! The pot falls down. Water and mud go everywhere. Lola and Paco are angry.",
    "end": "Mama Axolotl comes to the mess. She does not yell. She gives them two small shovels. 'Clean up now,' she says. The children clean up the mud together.",
    "image_keys": [
      "axolotl_lola_hiding_in_pot",
      "axolotl_paco_finding_lola",
      "axolotl_siblings_tugging_pot_mess",
      "axolotl_mama_shovel_resolution"
    ],
    "questions": [
      {
        "question": "Where did Lola hide?",
        "options": [
          "Under a rock",
          "Flowerpot",
          "In the weeds",
          "Behind Mama"
        ],
        "answer_c": "Flowerpot"
      },
      {
        "question": "What did the pot spill when it fell?",
        "options": [
          "Gold coins",
          "Water and mud",
          "Colored paint",
          "Small fish"
        ],
        "answer_c": "Water and mud"
      },
      {
        "question": "What did Mama Axolotl give them?",
        "options": [
          "A new pot",
          "Two small shovels",
          "A big towel",
          "More marbles"
        ],
        "answer_c": "Two small shovels"
      }
    ]
  },
  {
    "id": 3,
    "title": "The Secret Box",
    "beginning": "Juanito and Paco go to school. Today they take a new way. Paco says, 'Let's go where the sign says danger!' Juanito thinks it is a fun idea.",
    "development_1": "They swim in a dark path. The path has long, green kelp. Paco bumps his nose on something hard. It is an old metal box.",
    "development_2": "They work hard to open the box. It is not easy. Inside, there is no gold. They find shiny marbles and bottle caps! It is a colorful treasure.",
    "end": "They swim to school with the colorful marbles. They are late, but happy. Their teacher is Mr. Gillbert. He uses the marbles to teach the class. They love the new school path.",
    "image_keys": [
      "axolotl_friends_starting_detour",
      "axolotl_paco_tripping_over_box",
      "axolotl_opening_box_marbles",
      "axolotl_teacher_using_marbles_class"
    ],
    "questions": [
      {
        "question": "What was the warning word on the sign?",
        "options": [
          "Stop",
          "Go",
          "Danger",
          "School"
        ],
        "answer_c": "Danger"
      },
      {
        "question": "What did Paco bump his nose on?",
        "options": [
          "A big rock",
          "An old metal box",
          "Mr. Gillbert",
          "A piece of wood"
        ],
        "answer_c": "An old metal box"
      },
      {
        "question": "What did they find inside the box?",
        "options": [
          "Gold rings",
          "Shiny toys",
          "Marbles and bottle caps",
          "Old shells"
        ],
        "answer_c": "Marbles and bottle caps"
      }
    ]
  }
]
"""