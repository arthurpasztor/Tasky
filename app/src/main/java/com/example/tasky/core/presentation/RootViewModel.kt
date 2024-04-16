package com.example.tasky.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.Result
import com.example.tasky.main.data.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RootViewModel(private val repository: ApiRepository): ViewModel() {

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val response = repository.authenticate()
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
