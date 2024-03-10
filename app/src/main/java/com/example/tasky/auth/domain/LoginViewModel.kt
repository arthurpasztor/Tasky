package com.example.tasky.auth.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tasky.auth.presentation.isEmailValid

class LoginViewModel : ViewModel() {

    var emailText by mutableStateOf("")
        private set
    var passwordText by mutableStateOf("")
        private set

    fun updateEmail(new: String) {
        emailText = new
    }

    fun isEmailValid() = emailText.isEmailValid()

    fun updatePassword(new: String) {
        passwordText = new
    }

    fun logIn() {
        // TODO implement
    }
}