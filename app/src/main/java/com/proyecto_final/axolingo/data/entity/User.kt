package com.proyecto_final.axolingo.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User (
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "user") val user: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "sc_spell") val sc_spell: Float?,
    @ColumnInfo(name = "sc_reading") val sc_reading: Float?,
    @ColumnInfo(name = "sc_vocab") val sc_vocab: Float?,
    @ColumnInfo(name = "sc_transl") val sc_transl: Float?,
    @ColumnInfo(name = "sc_board") val sc_board: Float?,
    @ColumnInfo(name = "sc_shapes") val sc_shapes: Float?
)