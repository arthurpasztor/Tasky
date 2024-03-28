package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.data.AuthRepository
import com.example.tasky.auth.data.AuthResult
import com.example.tasky.auth.data.dto.LoginRequest
import com.example.tasky.auth.data.dto.SignUpRequest
import com.example.tasky.auth.domain.isEmailValid
import com.example.tasky.auth.domain.isNameValid
import com.example.tasky.auth.domain.isPasswordValid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

class SignUpViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<SignUpAuthAction>()
    val navChannel = _navChannel.receiveAsFlow()

    private val repository: AuthRepository by inject(AuthRepository::class.java)

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.NavigateBack -> navigateBack()
            SignUpAction.SignUp -> signUp()
            is SignUpAction.UpdateName -> _state.update {
                it.copy(
                    nameText = action.name,
                    isNameValid = action.name.isNameValid(),
                    shouldShowNameValidationError = !action.name.isNameValid(),
                    isActionButtonEnabled = action.name.isNameValid() && it.isEmailValid && it.isPasswordValid
                )
            }
            is SignUpAction.UpdateEmail -> _state.update {
                it.copy(
                    emailText = action.email,
                    isEmailValid = action.email.isEmailValid(),
                    shouldShowEmailValidationError = !action.email.isEmailValid(),
                    isActionButtonEnabled = it.isNameValid && action.email.isEmailValid() && it.isPasswordValid
                )
            }
            is SignUpAction.UpdatePassword -> _state.update {
                it.copy(
                    passwordText = action.password,
                    isPasswordValid = action.password.isPasswordValid(),
                    shouldShowPasswordValidationError = !action.password.isPasswordValid(),
                    isActionButtonEnabled = it.isNameValid && it.isEmailValid && action.password.isPasswordValid()
                )
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val payload = SignUpRequest(
                fullName = _state.value.nameText,
                email = _state.value.emailText,
                password = _state.value.passwordText
            )
            val response = repository.signUp(payload)

            if (response is AuthResult.Authorized<*> && response.data is LoginRequest) {
                loginAfterSignUp(response.data)
            } else {
                _state.update { it.copy(isLoading = false) }

                _navChannel.send(SignUpAuthAction.HandleAuthResponse(response))
            }
        }
    }

    private fun loginAfterSignUp(payload: LoginRequest) {
        viewModelScope.launch {
            val response = repository.login(payload)
            _navChannel.send(SignUpAuthAction.HandleAuthResponse(response))

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navChannel.send(SignUpAuthAction.NavigateBack)
        }
    }
}

data class SignUpState(
    val isLoading: Boolean = false,

    val nameText: String = "",
    val emailText: String = "",
    val passwordText: String = "",

    val isNameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false,

    val shouldShowNameValidationError: Boolean = false,
    val shouldShowEmailValidationError: Boolean = false,
    val shouldShowPasswordValidationError: Boolean = false,

    val isActionButtonEnabled: Boolean = false
)

sealed class SignUpAuthAction {
    data object NavigateBack: SignUpAuthAction()
    class HandleAuthResponse(val result: AuthResult): SignUpAuthAction()
}
