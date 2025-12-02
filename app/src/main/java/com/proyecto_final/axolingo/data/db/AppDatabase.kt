package com.proyecto_final.axolingo.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.proyecto_final.axolingo.data.dao.UserDao
import com.proyecto_final.axolingo.data.entity.User

// Clase abstracta que representa la base de datos de la aplicación
@Database(entities = [User::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    // Método abstracto para obtener el DAO de usuarios
    abstract fun userDao() : UserDao

    companion object {
        private const val DATABASE_NAME = "axolingo_db" // Nombre de la base de datos
        @Volatile
        private var INSTANCE: AppDatabase? = null // Instancia única de la base de datos

        // Método para obtener la instancia de la base de datos
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                                context.applicationContext,
                                AppDatabase::class.java,
                                DATABASE_NAME
                            )
                    .fallbackToDestructiveMigration(false) // Manejo de migraciones
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}