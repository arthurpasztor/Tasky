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

class AgendaViewModel(
    private val repository: ApiRepository,
    private val prefs: Preferences
) : ViewModel() {

    private val _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<AgendaResponseAction>()
    val navChannel = _navChannel.receiveAsFlow()

    init {
        _state.update {
            it.copy(
                userName = prefs.getString(Preferences.KEY_USER_NAME, "")
            )
        }
    }

    fun onAction(action: AgendaAction) {
        when (action) {
            AgendaAction.LogOut -> logOut()
            AgendaAction.ClearUserData -> clearUserData()
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            val response = repository.logout()
            _navChannel.send(AgendaResponseAction.HandleLogoutResponse(response))
        }
    }

    private fun clearUserData() {
        prefs.removeAll()
        prefs.removeEncrypted(Preferences.KEY_TOKEN)
    }
}

data class AgendaState(
    val userName: String = "",
    val month: String = "March"
)

sealed class AgendaResponseAction {
    class HandleLogoutResponse(val result: Result<Unit, RootError>) : AgendaResponseAction()
}