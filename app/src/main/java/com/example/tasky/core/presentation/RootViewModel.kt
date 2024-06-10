package com.example.tasky.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.core.domain.Result
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
            val response = repository.authenticate()
            _isCheckingAuthentication.value = false
            when (response) {
                is Result.Success -> {
                    _isLoggedIn.value = true
                }
                is Result.Error -> {
                    _isLoggedIn.value = false
                }
            }
        }
    }
}
