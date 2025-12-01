package com.proyecto_final.axolingo.selector_palabras

// Clase de datos que representa un ejercicio de traducción
// Contiene la frase original, las palabras disponibles y la respuesta correcta
data class Ejercicio(
    val sentence: String, // Frase original a traducir
    val words: List<String>, // Palabras disponibles para formar la respuesta
    val answer: String // Respuesta correcta esperada
)
