package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.AuthRepository
import com.example.tasky.auth.domain.isEmailValid
import com.example.tasky.core.domain.RootError
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<LoginAuthAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            LoginAction.LogIn -> logIn()
            LoginAction.NavigateToSignUp -> navigateToSignUpScreen()
            is LoginAction.UpdateEmail -> _state.update {
                it.copy(
                    emailText = action.email,
                    isEmailValid = action.email.isEmailValid(),
                    shouldShowEmailValidationError = !action.email.isEmailValid(),
                    isActionButtonEnabled = action.email.isEmailValid()
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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            repository.login(
                email = _state.value.emailText,
                password = _state.value.passwordText
            )
                .onSuccess {
                    _state.update { it.copy(isLoading = false) }

                    _navChannel.send(LoginAuthAction.HandleAuthResponseSuccess)
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }

                    _navChannel.send(LoginAuthAction.HandleAuthResponseError(error))
                }
        }
    }

    private fun navigateToSignUpScreen() {
        viewModelScope.launch {
            _navChannel.send(LoginAuthAction.NavigateToSignUpScreen)
        }
    }
}

data class LoginState(
    val isLoading: Boolean = false,

    val emailText: String = "",
    val passwordText: String = "",

    val isEmailValid: Boolean = false,

    val shouldShowEmailValidationError: Boolean = false,

    val isActionButtonEnabled: Boolean = false
)

sealed class LoginAuthAction {
    data object NavigateToSignUpScreen : LoginAuthAction()
    data object HandleAuthResponseSuccess : LoginAuthAction()
    class HandleAuthResponseError(val error: RootError) : LoginAuthAction()
}