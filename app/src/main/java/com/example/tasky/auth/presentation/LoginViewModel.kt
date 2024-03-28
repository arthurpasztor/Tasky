package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.data.AuthRepository
import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.auth.domain.isEmailValid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class LoginViewModel : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<LoginAuthAction>()
    val navChannel = _navChannel.receiveAsFlow()

    private val repository: AuthRepository by inject(AuthRepository::class.java)

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

            val payload = LoginRequest(_state.value.emailText, _state.value.passwordText)
            val response = repository.login(payload)
            _navChannel.send(LoginAuthAction.HandleAuthResponse(response))

            _state.update { it.copy(isLoading = false) }
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
    data object NavigateToSignUpScreen: LoginAuthAction()
    class HandleAuthResponse(val result: Result<Unit, RootError>): LoginAuthAction()
}