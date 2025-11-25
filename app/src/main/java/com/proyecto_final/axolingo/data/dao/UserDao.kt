package com.proyecto_final.axolingo.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.proyecto_final.axolingo.data.entity.User

@Dao
interface UserDao {
    @Query("SELECT uid FROM users WHERE user = :userName LIMIT 1")
    suspend fun findUserByName(userName: String): Int?

    @Query("SELECT email FROM users WHERE email = :userEmail LIMIT 1")
    suspend fun findEmailById(userEmail: String): String?

    @Query("SELECT * FROM users WHERE user = :userData OR email = :userData LIMIT 1")
    suspend fun findUserToLogin(userData: String): User?

    @Query("UPDATE users SET password = :password WHERE user = :user")
    suspend fun updatePassByUser(user: String, password: String): Int

    @Query("UPDATE users SET password = :password WHERE email = :email")
    suspend fun updatePassByEmail(email: String, password: String): Int

    @Query("UPDATE users SET sc_spell = :score WHERE user = :user")
    suspend fun updateSCSpell(user: String, score: Float): Int

    @Query("UPDATE users SET sc_reading = :score WHERE user = :user")
    suspend fun updateSCReading(user: String, score: Float): Int

    @Query("UPDATE users SET sc_vocab = :score WHERE user = :user")
    suspend fun updateSCVocab(user: String, score: Float): Int

    @Query("UPDATE users SET sc_transl = :score WHERE user = :user")
    suspend fun updateSCTransl(user: String, score: Float): Int

    @Query("UPDATE users SET sc_board = :score WHERE user = :user")
    suspend fun updateSCBoard(user: String, score: Float): Int

    @Query("UPDATE users SET sc_shapes = :score WHERE user = :user")
    suspend fun updateSCShapes(user: String, score: Float): Int

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(vararg user: User)

    @Delete
    suspend fun delete(user: User)
}