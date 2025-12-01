package com.proyecto_final.axolingo.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// ViewModel para manejar el cambio de contraseña
class ContraViewModel(private val userDao: UserDao) : ViewModel() {
    // Función para cambiar la contraseña de un usuario
    // user: Nombre de usuario
    // newPass: Nueva contraseña
    // onSuccess: Acción a realizar si el cambio es exitoso
    // onConflict: Acción a realizar si ocurre un conflicto o error
    fun cambiarContra(user: String, newPass: String, onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUser = userDao.findUserToLogin(user) // Buscar el usuario en la base de datos
                if (existingUser != null) {
                    val updatePass = userDao.updatePassByUser(user, newPass) // Actualizar la contraseña
                    if (updatePass > 0) {
                        onSuccess() // Éxito
                    } else {
                        onConflict() // Conflicto
                    }
                } else {
                    onConflict() // Usuario no encontrado
                }
            } catch (e: Exception) { onConflict() } // Manejo de errores
        }
    }
}