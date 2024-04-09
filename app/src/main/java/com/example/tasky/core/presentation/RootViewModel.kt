package com.example.tasky.core.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.main.data.ApiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent

class RootViewModel: ViewModel() {

    private val _navChannel = Channel<Result<Unit, RootError>>()
    val navChannel = _navChannel.receiveAsFlow()

    private val repository: ApiRepository by KoinJavaComponent.inject(ApiRepository::class.java)

    fun checkTokenValid() {
        viewModelScope.launch {
            val response = repository.authenticate()
            _navChannel.send(response)
        }
    }
}
