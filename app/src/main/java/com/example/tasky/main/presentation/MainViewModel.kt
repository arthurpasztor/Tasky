package com.example.tasky.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.Preferences
import com.example.tasky.main.data.ApiRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.java.KoinJavaComponent.inject

// FYI, MainViewModel is for testing purposes only, will be changed
class MainViewModel: ViewModel() {
    private val _state = MutableStateFlow(MainState())

    val state = _state.asStateFlow()
    private val _navChannel = Channel<MainDummyAction>()

    val navChannel = _navChannel.receiveAsFlow()
    private val prefs: Preferences by inject(Preferences::class.java)
    private val repository: ApiRepository by inject(ApiRepository::class.java)

    init {
        _state.update {
            it.copy(
                userName = prefs.getString(Preferences.KEY_USER_NAME, "")
            )
        }
    }

    fun onAction(action: MainAction) {
        when (action) {
            MainAction.LogOut -> logOut()
            MainAction.ClearUserData -> clearUserData()
            MainAction.TestApi -> testAPI()
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            val response = repository.logout()
            _navChannel.send(MainDummyAction.HandleLogoutResponse(response))
        }
    }

    private fun clearUserData() {
        prefs.removeAll()
        prefs.removeEncrypted(Preferences.KEY_TOKEN)
    }

    private fun testAPI() {
        viewModelScope.launch {
            repository.authenticate()
        }
    }
}

data class MainState(val userName: String = "")

sealed class MainDummyAction {
    class HandleLogoutResponse(val result: Result<Unit, RootError>): MainDummyAction()
}