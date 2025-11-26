package com.proyecto_final.axolingo.forms

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.proyecto_final.axolingo.data.dao.UserDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContraViewModel(private val userDao: UserDao) : ViewModel() {
    fun cambiarContra(user: String, newPass: String, onSuccess: () -> Unit, onConflict: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingUser = userDao.findUserToLogin(user)
                if (existingUser != null) {
                    val updatePass = userDao.updatePassByUser(user, newPass)
                    if (updatePass > 0) {
                        onSuccess()
                    } else {
                        onConflict()
                    }
                } else {
                    onConflict()
                }
            } catch (e: Exception) { onConflict() }
        }
    }
}