package com.example.tasky.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.data.Preferences
import com.example.tasky.main.data.ApiRepository
import com.example.tasky.main.data.dto.AgendaDTO
import com.example.tasky.main.domain.getUTCMillis
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

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

        loadDailyAgenda()
    }

    fun onAction(action: AgendaAction) {
        when (action) {
            AgendaAction.LogOut -> logOut()
            AgendaAction.ClearUserData -> clearUserData()
            is AgendaAction.UpdateSelectedDate -> {
                updateSelectedDate(action.newSelection, action.forceSelectedDateToFirstPosition)
            }
            AgendaAction.CreateNewEvent -> createNewEvent()
            AgendaAction.CreateNewTask -> createNewTask()
            AgendaAction.CreateNewReminder -> createNewReminder()
        }
    }

    private fun updateSelectedDate(date: LocalDate, forceSelectedDateToFirstPosition: Boolean) {
        if (forceSelectedDateToFirstPosition) {
            _state.update {
                it.copy(
                    selectedDate = date,
                    firstDateOfHeader = date
                )
            }
        } else {
            _state.update {
                it.copy(
                    selectedDate = date
                )
            }
        }

        loadDailyAgenda()
    }

    private fun loadDailyAgenda() {
        viewModelScope.launch {
            val response = repository.getDailyAgenda(_state.value.selectedDate.getUTCMillis())

            _state.update {
                when (response) {
                    is Result.Success -> it.copy(
                        dailyAgenda = response.data,
                        dailyAgendaError = null
                    )
                    is Result.Error -> it.copy(
                        dailyAgenda = AgendaDTO.getEmpty(),
                        dailyAgendaError = response.error
                    )
                }
            }
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            val response = repository.logout()
            _navChannel.send(AgendaResponseAction.HandleLogoutResponse(response))
        }
    }

    private fun createNewEvent() {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.CreateNewEventAction)
        }
    }

    private fun createNewTask() {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.CreateNewTaskAction)
        }
    }

    private fun createNewReminder() {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.CreateNewReminderAction)
        }
    }

    private fun clearUserData() {
        prefs.removeAll()
        prefs.removeEncrypted(Preferences.KEY_ACCESS_TOKEN)
        prefs.removeEncrypted(Preferences.KEY_REFRESH_TOKEN)
    }
}

data class AgendaState(
    val userName: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val firstDateOfHeader: LocalDate = LocalDate.now(),
    val dailyAgenda: AgendaDTO = AgendaDTO.getEmpty(),
    val dailyAgendaError: RootError? = null
)

sealed class AgendaResponseAction {
    class HandleLogoutResponse(val result: Result<Unit, RootError>) : AgendaResponseAction()
    data object CreateNewEventAction : AgendaResponseAction()
    data object CreateNewTaskAction : AgendaResponseAction()
    data object CreateNewReminderAction : AgendaResponseAction()
}