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

    @Insert
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(vararg user: User)

    @Delete
    suspend fun delete(user: User)
}