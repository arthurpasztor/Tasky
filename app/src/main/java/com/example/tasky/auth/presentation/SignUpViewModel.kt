package com.example.tasky.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.isEmailValid
import com.example.tasky.auth.domain.isNameValid
import com.example.tasky.auth.domain.isPasswordValid
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<SignUpNav>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            SignUpAction.NavigateBack -> navigateBack()
            SignUpAction.SignUp -> signUp()
            is SignUpAction.UpdateName -> _state.update {
                it.copy(
                    nameText = action.name,
                    isNameValid = action.name.isNameValid()
                )
            }
            is SignUpAction.UpdateEmail -> _state.update {
                it.copy(
                    emailText = action.email,
                    isEmailValid = action.email.isEmailValid()
                )
            }
            is SignUpAction.UpdatePassword -> _state.update {
                it.copy(
                    passwordText = action.password,
                    isPasswordValid = action.password.isPasswordValid()
                )
            }
        }
    }

    private fun signUp() {
        // TODO implement
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navChannel.send(SignUpNav.NavigateBack)
        }
    }
}

data class SignUpState(
    val nameText: String = "",
    val emailText: String = "",
    val passwordText: String = "",

    val isNameValid: Boolean = false,
    val isEmailValid: Boolean = false,
    val isPasswordValid: Boolean = false
)

sealed class SignUpNav {
    data object NavigateBack: SignUpNav()
}
