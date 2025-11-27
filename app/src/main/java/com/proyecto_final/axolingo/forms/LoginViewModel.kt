package com.proyecto_final.axolingo.forms


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import com.proyecto_final.axolingo.data.entity.User
import com.proyecto_final.axolingo.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val userDao: UserDao, private val sessionManager: SessionManager) : ViewModel() {
    fun loginUsuario(userData: String, userPass: String, onSuccess: (User) -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUser = userDao.findUserToLogin(userData)
                if (existingUser != null) {
                    if (userPass != existingUser.password) {
                        onConflict()
                    } else {
                        sessionManager.saveLoginState(userData)
                        onSuccess(existingUser)
                    }
                } else {
                    onConflict()
                }
            } catch (e: Exception) { onConflict }
        }
    }

    fun logoutUsuario(onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sessionManager.clearSession()
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onConflict()
                }
            }
        }
    }
}