package com.proyecto_final.axolingo.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import com.proyecto_final.axolingo.data.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RegistroViewModel(private val userDao: UserDao) : ViewModel() {
    fun registrarUsuario(user: User, onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUserId = userDao.findUserByName(user.user ?: "")
                if (existingUserId != null) {
                    onConflict()
                } else {
                    val insertId = userDao.insertUser(user)
                    if (insertId > 0) {
                        onSuccess()
                    } else {
                        onConflict()
                    }
                }
            } catch (e: Exception) { onConflict }
        }
    }
}