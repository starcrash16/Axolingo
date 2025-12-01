package com.proyecto_final.axolingo.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import com.proyecto_final.axolingo.data.entity.User
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel para manejar el inicio y cierre de sesión
class LoginViewModel(private val userDao: UserDao, private val sessionManager: SessionManager) : ViewModel() {
    // Función para iniciar sesión
    // userData: Nombre de usuario o correo
    // userPass: Contraseña del usuario
    // onSuccess: Acción a realizar si el inicio de sesión es exitoso
    // onConflict: Acción a realizar si ocurre un conflicto o error
    fun loginUsuario(userData: String, userPass: String, onSuccess: (User) -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUser = userDao.findUserToLogin(userData) // Buscar el usuario en la base de datos
                if (existingUser != null) {
                    if (userPass != existingUser.password) {
                        onConflict() // Contraseña incorrecta
                    } else {
                        sessionManager.saveLoginState(userData) // Guardar el estado de sesión
                        onSuccess(existingUser) // Éxito
                    }
                } else {
                    onConflict() // Usuario no encontrado
                }
            } catch (e: Exception) { onConflict() } // Manejo de errores
        }
    }

    // Función para cerrar sesión
    // onSuccess: Acción a realizar si el cierre de sesión es exitoso
    // onConflict: Acción a realizar si ocurre un conflicto o error
    fun logoutUsuario(onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sessionManager.clearSession() // Limpiar la sesión
                withContext(Dispatchers.Main) {
                    onSuccess() // Éxito
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onConflict() // Manejo de errores
                }
            }
        }
    }
}