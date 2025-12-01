package com.proyecto_final.axolingo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.proyecto_final.axolingo.data.entity.User

// Interfaz DAO para realizar operaciones en la tabla de usuarios
@Dao
interface UserDao {
    // Buscar el ID de un usuario por su nombre
    @Query("SELECT uid FROM users WHERE user = :userName LIMIT 1")
    suspend fun findUserByName(userName: String): Int?

    // Buscar el correo electrónico de un usuario por su email
    @Query("SELECT email FROM users WHERE email = :userEmail LIMIT 1")
    suspend fun findEmailById(userEmail: String): String?

    // Buscar un usuario para iniciar sesión por nombre o correo
    @Query("SELECT * FROM users WHERE user = :userData OR email = :userData LIMIT 1")
    suspend fun findUserToLogin(userData: String): User?

    // Actualizar la contraseña de un usuario por su nombre
    @Query("UPDATE users SET password = :password WHERE user = :user")
    suspend fun updatePassByUser(user: String, password: String): Int

    // Actualizar la contraseña de un usuario por su correo
    @Query("UPDATE users SET password = :password WHERE email = :email")
    suspend fun updatePassByEmail(email: String, password: String): Int

    // Actualizar la puntuación de ortografía de un usuario
    @Query("UPDATE users SET sc_spell = :score WHERE user = :user")
    suspend fun updateSCSpell(user: String, score: Float): Int

    // Actualizar la puntuación de lectura de un usuario
    @Query("UPDATE users SET sc_reading = :score WHERE user = :user")
    suspend fun updateSCReading(user: String, score: Float): Int

    // Actualizar la puntuación de vocabulario de un usuario
    @Query("UPDATE users SET sc_vocab = :score WHERE user = :user")
    suspend fun updateSCVocab(user: String, score: Float): Int

    // Actualizar la puntuación de traducción de un usuario
    @Query("UPDATE users SET sc_transl = :score WHERE user = :user")
    suspend fun updateSCTransl(user: String, score: Float): Int

    // Actualizar la puntuación de tablero de un usuario
    @Query("UPDATE users SET sc_board = :score WHERE user = :user")
    suspend fun updateSCBoard(user: String, score: Float): Int

    // Actualizar la puntuación de figuras de un usuario
    @Query("UPDATE users SET sc_shapes = :score WHERE user = :user")
    suspend fun updateSCShapes(user: String, score: Float): Int

    // Insertar un nuevo usuario en la base de datos
    @Insert
    suspend fun insertUser(user: User): Long

    // Actualizar uno o más usuarios
    @Update
    suspend fun updateUser(vararg user: User)

    // Eliminar un usuario de la base de datos
    @Delete
    suspend fun delete(user: User)
}