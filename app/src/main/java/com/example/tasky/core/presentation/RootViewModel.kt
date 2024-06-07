package com.example.tasky.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RootViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    private val _isCheckingAuthentication = MutableStateFlow(true)
    val isCheckingAuthentication = _isCheckingAuthentication.asStateFlow()

    init {
        viewModelScope.launch {
            _isCheckingAuthentication.value = false
            repository.authenticate()
                .onSuccess {
                    _isLoggedIn.value = true
                }
                .onError {
                    _isLoggedIn.value = false
                }
        }
    }
}
