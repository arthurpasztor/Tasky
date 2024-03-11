package com.example.tasky.auth.domain

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.tasky.auth.presentation.isEmailValid

class SignUpViewModel : ViewModel() {

    var nameText by mutableStateOf("")
        private set
    var emailText by mutableStateOf("")
        private set
    var passwordText by mutableStateOf("")
        private set

    fun updateNameText(new: String) {
        nameText = new
    }

    fun updateEmail(new: String) {
        emailText = new
    }

    fun updatePassword(new: String) {
        passwordText = new
    }

    fun isNameValid() = nameText.isNotBlank()

    fun isEmailValid() = emailText.isEmailValid()

    fun signUp() {
        // TODO implement
    }
}