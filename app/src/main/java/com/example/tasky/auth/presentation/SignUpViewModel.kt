package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.AuthRepository
import com.example.tasky.auth.domain.Login
import com.example.tasky.auth.domain.NameError
import com.example.tasky.auth.domain.PasswordError
import com.example.tasky.auth.domain.SignUp
import com.example.tasky.core.domain.Result
import com.example.tasky.core.domain.RootError
import com.example.tasky.auth.domain.isEmailValid
import com.example.tasky.auth.domain.validateName
import com.example.tasky.auth.domain.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<SignUpAuthAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.NavigateBack -> navigateBack()
            SignUpAction.SignUp -> signUp()
            is SignUpAction.UpdateName -> _state.update {
                val validateName = action.name.validateName()
                it.copy(
                    nameText = action.name,
                    isNameValid = validateName !is Result.Error,
                    nameValidationError = if (validateName is Result.Error) validateName.error else null,
                    isActionButtonEnabled = validateName !is Result.Error && it.isEmailValid && it.isPasswordValid
                )
            }

            is SignUpAction.UpdateEmail -> _state.update {
                it.copy(
                    emailText = action.email,
                    isEmailValid = action.email.isEmailValid(),
                    emailValidationError = !action.email.isEmailValid(),
                    isActionButtonEnabled = it.isNameValid && action.email.isEmailValid() && it.isPasswordValid
                )
            }

            is SignUpAction.UpdatePassword -> _state.update {
                val validatePassword = action.password.validatePassword()
                it.copy(
                    passwordText = action.password,
                    isPasswordValid = validatePassword !is Result.Error,
                    passwordValidationError = if (validatePassword is Result.Error) validatePassword.error else null,
                    isActionButtonEnabled = it.isNameValid && it.isEmailValid && validatePassword !is Result.Error
                )
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val payload = SignUp(
                fullName = _state.value.nameText,
                email = _state.value.emailText,
                password = _state.value.passwordText
            )
            val response = repository.signUp(payload)

            if (response is Result.Success<*> && response.data is Login) {
                loginAfterSignUp(response.data)
            } else {
                _state.update { it.copy(isLoading = false) }

                _navChannel.send(SignUpAuthAction.HandleAuthResponse(response))
            }
        }
    }

    private fun loginAfterSignUp(payload: Login) {
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

    val nameValidationError: NameError? = null,
    val emailValidationError: Boolean = false,
    val passwordValidationError: PasswordError? = null,

    val isActionButtonEnabled: Boolean = false
)

sealed class SignUpAuthAction {
    data object NavigateBack : SignUpAuthAction()
    class HandleAuthResponse(val result: Result<*, RootError>) : SignUpAuthAction()
}
