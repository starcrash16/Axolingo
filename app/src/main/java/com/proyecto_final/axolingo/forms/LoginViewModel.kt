package com.proyecto_final.axolingo.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginViewModel(private val userDao: UserDao) : ViewModel() {
    fun loginUsuario(userData: String, userPass: String, onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUser = userDao.findUserToLogin(userData)
                if (existingUser != null) {
                    if (userPass != existingUser.password) {
                        onConflict()
                    } else {
                        onSuccess()
                    }
                } else {
                    onConflict()
                }
            } catch (e: Exception) { onConflict }
        }
    }
}