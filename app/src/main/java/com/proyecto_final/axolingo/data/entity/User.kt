package com.proyecto_final.axolingo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa la tabla de usuarios en la base de datos
@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0, // ID único del usuario
    @ColumnInfo(name = "user") val user: String?, // Nombre de usuario
    @ColumnInfo(name = "email") val email: String?, // Correo electrónico del usuario
    @ColumnInfo(name = "password") val password: String?, // Contraseña del usuario
    @ColumnInfo(name = "sc_spell") val sc_spell: Float?, // Puntuación de ortografía
    @ColumnInfo(name = "sc_reading") val sc_reading: Float?, // Puntuación de lectura
    @ColumnInfo(name = "sc_vocab") val sc_vocab: Float?, // Puntuación de vocabulario
    @ColumnInfo(name = "sc_transl") val sc_transl: Float?, // Puntuación de traducción
    @ColumnInfo(name = "sc_board") val sc_board: Float?, // Puntuación de tablero
    @ColumnInfo(name = "sc_shapes") val sc_shapes: Float? // Puntuación de figuras
)