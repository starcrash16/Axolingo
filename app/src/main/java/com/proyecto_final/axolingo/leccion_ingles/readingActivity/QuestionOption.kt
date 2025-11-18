// File: StoryData.kt (Data Classes for JSON Parsing)

package com.proyecto_final.axolingo.leccion_ingles.readingActivity

// Represents a single multiple-choice option (though not strictly necessary, good practice)
data class QuestionOption(
    val question: String,
    val options: List<String>,
    val answer_c: String
)

// Represents the full structure of one story
data class Story(
    val id: Int,
    val title: String,
    val beginning: String,
    val development_1: String,
    val development_2: String,
    val end: String,
    val image_keys: List<String>,
    val questions: List<QuestionOption>
)

// Main structure: A list of stories
data class StoryList(
    val stories: List<Story>
)