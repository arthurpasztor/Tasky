package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import com.example.tasky.auth.domain.isEmailValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    fun updateEmail(new: String) {
        _state.value.emailText = new
    }

    fun updatePassword(new: String) {
        _state.value.passwordText = new
    }

    fun logIn() {
        // TODO implement
    }
}

data class LoginState(
    var emailText: String = "",
    var passwordText: String = ""
) {
    fun isEmailValid() = emailText.isEmailValid()

}