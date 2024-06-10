package com.example.tasky.agenda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.agenda.domain.getUTCMillis
import com.example.tasky.agenda.domain.isToday
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.core.data.Preferences
import com.example.tasky.core.domain.DataError
import com.example.tasky.core.domain.RootError
import com.example.tasky.core.domain.onError
import com.example.tasky.core.domain.onSuccess
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class AgendaViewModel(
    private val authRepo: AuthRepository,
    private val agendaRepo: AgendaRepository,
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

            AgendaAction.PullToRefresh -> {
                _state.update { it.copy(isRefreshing = true) }
                loadDailyAgenda(triggerFromPullToRefresh = true)
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
                    isSelectedDateToday = date.isToday(),
                    firstDateOfHeader = date,
                )
            }
        } else {
            _state.update {
                it.copy(
                    selectedDate = date,
                    isSelectedDateToday = date.isToday(),
                )
            }
        }

        loadDailyAgenda()
    }

    private fun loadDailyAgenda(triggerFromPullToRefresh: Boolean = false) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            agendaRepo.getDailyAgenda(_state.value.selectedDate.getUTCMillis())
                .onSuccess { agenda ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            dailyAgenda = agenda,
                            dailyAgendaError = null
                        )
                    }
                }
                .onError { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            dailyAgenda = Agenda.getEmpty(),
                            dailyAgendaError = error
                        )
                    }
                }

            if (triggerFromPullToRefresh) {
                _state.update { it.copy(isRefreshing = false) }
            }
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            authRepo.logout()
                .onSuccess {
                    _navChannel.send(AgendaResponseAction.HandleLogoutResponseSuccess)
                }
                .onError {
                    _navChannel.send(AgendaResponseAction.HandleLogoutResponseError(it))
                }
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
    val isLoading: Boolean = false,
    val userName: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val isSelectedDateToday: Boolean = false,
    val firstDateOfHeader: LocalDate = LocalDate.now(),
    val dailyAgenda: Agenda = Agenda.getSample(),
    val dailyAgendaError: RootError? = null,
    val isRefreshing: Boolean = false
)

sealed class AgendaResponseAction {
    data object HandleLogoutResponseSuccess : AgendaResponseAction()
    class HandleLogoutResponseError(val error: DataError) : AgendaResponseAction()
    data object CreateNewEventAction : AgendaResponseAction()
    data object CreateNewTaskAction : AgendaResponseAction()
    data object CreateNewReminderAction : AgendaResponseAction()
}