package com.proyecto_final.axolingo.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import com.proyecto_final.axolingo.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel para manejar el registro de nuevos usuarios
class RegistroViewModel(private val userDao: UserDao) : ViewModel() {
    // Función para registrar un nuevo usuario
    // user: Objeto User con los datos del nuevo usuario
    // onSuccess: Acción a realizar si el registro es exitoso
    // onConflict: Acción a realizar si ocurre un conflicto o error
    fun registrarUsuario(user: User, onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUserId = userDao.findUserByName(user.user ?: "") // Verificar si el usuario ya existe
                if (existingUserId != null) {
                    onConflict() // Usuario ya registrado
                } else {
                    val insertId = userDao.insertUser(user) // Insertar el nuevo usuario
                    if (insertId > 0) {
                        onSuccess() // Éxito
                    } else {
                        onConflict() // Conflicto
                    }
                }
            } catch (e: Exception) { onConflict() } // Manejo de errores
        }
    }
}