// Archivo: StoryData.kt (Clases de datos para el análisis de JSON)

package com.proyecto_final.axolingo.leccion_ingles.readingActivity

// Representa una única opción de selección múltiple (aunque no es estrictamente necesario, es una buena práctica)
data class QuestionOption(
    val question: String, // Pregunta del cuestionario
    val options: List<String>, // Lista de opciones disponibles
    val answer_c: String // Respuesta correcta
)

// Representa la estructura completa de una historia
data class Story(
    val id: Int, // ID único de la historia
    val title: String, // Título de la historia
    val beginning: String, // Introducción de la historia
    val development_1: String, // Desarrollo parte 1
    val development_2: String, // Desarrollo parte 2
    val end: String, // Conclusión de la historia
    val image_keys: List<String>, // Claves de imágenes asociadas a la historia
    val questions: List<QuestionOption> // Lista de preguntas asociadas a la historia
)

// Estructura principal: Una lista de historias
data class StoryList(
    val stories: List<Story> // Lista de todas las historias
)