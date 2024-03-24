package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.isEmailValid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<LoginNav>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.LogIn -> logIn()
            LoginAction.NavigateToSignUp -> navigateToSignUpScreen()
            is LoginAction.UpdateEmail -> _state.update {
                it.copy(
                    emailText = action.email,
                    isEmailValid = action.email.isEmailValid()
                )
            }
            is LoginAction.UpdatePassword -> _state.update {
                it.copy(
                    passwordText = action.password,
                )
            }
        }
    }

    private fun logIn() {
        // TODO implement
    }

    private fun navigateToSignUpScreen() {
        viewModelScope.launch {
            _navChannel.send(LoginNav.NavigateToSignUpScreen)
        }
    }
}

data class LoginState(
    val emailText: String = "",
    val passwordText: String = "",

    val isEmailValid: Boolean = false
)

sealed class LoginNav {
    data object NavigateToSignUpScreen: LoginNav()
}