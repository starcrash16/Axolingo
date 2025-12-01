package com.proyecto_final.axolingo.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Clase para gestionar la sesión del usuario utilizando DataStore
// Proporciona métodos para guardar el estado de inicio de sesión, recuperar el usuario actual y limpiar la sesión.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session_prefs")

class SessionManager(context: Context) {
    private val dataStore = context.dataStore

    companion object {
        // Claves para almacenar las preferencias
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in") // Indica si el usuario está logueado
        val USERNAME_KEY = stringPreferencesKey("username") // Almacena el nombre de usuario
    }

    // Guarda el estado de inicio de sesión y el nombre de usuario
    suspend fun saveLoginState(username: String) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = true // Marca al usuario como logueado
            preferences[USERNAME_KEY] = username // Guarda el nombre de usuario
        }
    }

    // Flujo para observar el estado de inicio de sesión y recuperar el nombre de usuario
    val loginFlow: Flow<String?> = dataStore.data.map { preferences ->
        val isLoggedIn = preferences[IS_LOGGED_IN] ?: false // Verifica si el usuario está logueado
        if (isLoggedIn) {
            preferences[USERNAME_KEY] // Devuelve el nombre de usuario si está logueado
        } else {
            null // Devuelve null si no está logueado
        }
    }

    // Limpia la sesión eliminando las preferencias almacenadas
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(IS_LOGGED_IN) // Elimina el estado de inicio de sesión
            preferences.remove(USERNAME_KEY) // Elimina el nombre de usuario
        }
    }
}