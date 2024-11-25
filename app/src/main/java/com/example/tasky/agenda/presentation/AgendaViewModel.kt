package com.example.tasky.agenda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AgendaAlarmScheduler
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.AgendaRepository
import com.example.tasky.agenda.domain.AuthRepository
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.NetworkConnectivityMonitor
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.getUTCMillis
import com.example.tasky.agenda.domain.isToday
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.model.AgendaListItem
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
import java.time.LocalDateTime

class AgendaViewModel(
    private val authRepo: AuthRepository,
    private val agendaRepo: AgendaRepository,
    private val eventRepo: EventRepository,
    private val taskRepo: TaskRepository,
    private val reminderRepo: ReminderRepository,
    private val prefs: Preferences,
    private val scheduler: AgendaAlarmScheduler,
    networkMonitor: NetworkConnectivityMonitor,
) : ViewModel() {

    private val _state = MutableStateFlow(AgendaState())
    val state = _state.asStateFlow()

    private val _navChannel = Channel<AgendaResponseAction>()
    val navChannel = _navChannel.receiveAsFlow()

    val networkState = networkMonitor.observeNetworkAvailability()

    init {
        _state.update {
            it.copy(
                userName = prefs.getString(Preferences.KEY_USER_NAME, "")
            )
        }

        if (!prefs.isOfflineActivity()) {
            loadDailyAgenda()
        }
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

            is AgendaAction.SetTaskDone -> setTaskDone(action.task.task)
            is AgendaAction.Delete -> deleteItem(action.itemId, action.itemType)
            is AgendaAction.Edit -> editAgendaItem(action.itemId, action.itemType)
            is AgendaAction.Open -> openAgendaItem(action.itemId, action.itemType)

            AgendaAction.SyncOfflineChanges -> syncOfflineChanges()
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

    private fun setTaskDone(task: AgendaListItem.Task) {
        val taskDone = task.copy(isDone = true)

        viewModelScope.launch {
            taskRepo.updateTask(taskDone)
                .onSuccess {
                    _state.update {
                        it.copy(
                            dailyAgenda = Agenda(
                                _state.value.dailyAgenda.items.map { currentItem ->
                                    if (currentItem is AgendaListItem.Task && currentItem.id == taskDone.id) {
                                        currentItem.copy(isDone = taskDone.isDone)
                                    } else {
                                        currentItem
                                    }
                                }.toMutableList()
                            )
                        )
                    }
                }
                .onError {
                    _navChannel.send(AgendaResponseAction.AgendaError(it))
                }
        }
    }

    private fun deleteItem(itemId: String, itemType: AgendaItemType) {
        viewModelScope.launch {
            when (itemType) {
                AgendaItemType.EVENT -> {
                    eventRepo.deleteEvent(itemId)
                        .onSuccess {
                            deleteItem(itemId)
                        }
                        .onError {
                            _navChannel.send(AgendaResponseAction.AgendaError(it))
                        }
                }

                AgendaItemType.TASK -> {
                    taskRepo.deleteTask(itemId)
                        .onSuccess {
                            deleteItem(itemId)
                        }
                        .onError {
                            _navChannel.send(AgendaResponseAction.AgendaError(it))
                        }
                }

                AgendaItemType.REMINDER -> {
                    reminderRepo.deleteReminder(itemId)
                        .onSuccess {
                            deleteItem(itemId)
                        }
                        .onError {
                            _navChannel.send(AgendaResponseAction.AgendaError(it))
                        }
                }
            }
        }
    }

    private fun deleteItem(itemId: String) {
        scheduler.cancelNotificationScheduler(itemId)

        _state.update {
            it.copy(
                dailyAgenda = _state.value.dailyAgenda.removeItem(itemId)
            )
        }
    }

    private fun syncOfflineChanges() {
        if (prefs.isOfflineActivity()) {
            _state.update { it.copy(isLoading = true) }

            viewModelScope.launch {
                val currentUserId = prefs.getEncryptedString(Preferences.KEY_USER_ID, "")

                agendaRepo.syncOfflineChanges(currentUserId)
                    .onSuccess {
                        prefs.setOfflineActivity(false)
                        _state.update { it.copy(isLoading = false) }
                        _navChannel.send(AgendaResponseAction.SyncOfflineChangesSuccessful)

                        loadDailyAgenda()
                    }
                    .onError { error ->
                        _state.update { it.copy(isLoading = false) }
                        _navChannel.send(AgendaResponseAction.AgendaError(error))
                    }
            }
        }
    }

    private fun openAgendaItem(itemId: String, itemType: AgendaItemType) {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.OpenAgendaItemDetail(itemId, itemType, false))
        }
    }

    private fun editAgendaItem(itemId: String, itemType: AgendaItemType) {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.OpenAgendaItemDetail(itemId, itemType, true))
        }
    }

    private fun logOut() {
        viewModelScope.launch {
            authRepo.logout()
                .onSuccess {
                    _navChannel.send(AgendaResponseAction.HandleLogoutResponseSuccess)
                }
                .onError {
                    _navChannel.send(AgendaResponseAction.AgendaError(it))
                }
        }
    }

    private fun createNewEvent() {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.CreateNewAgendaItem(AgendaItemType.EVENT))
        }
    }

    private fun createNewTask() {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.CreateNewAgendaItem(AgendaItemType.TASK))
        }
    }

    private fun createNewReminder() {
        viewModelScope.launch {
            _navChannel.send(AgendaResponseAction.CreateNewAgendaItem(AgendaItemType.REMINDER))
        }
    }

    private fun clearUserData() {
        viewModelScope.launch {
            authRepo.clearAllData()
        }
    }
}

data class AgendaState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val isSelectedDateToday: Boolean = false,
    val firstDateOfHeader: LocalDate = LocalDate.now(),
    val dailyAgenda: Agenda = Agenda(),
    val dailyAgendaError: RootError? = null,
    val isRefreshing: Boolean = false
)

sealed interface AgendaResponseAction {
    data object HandleLogoutResponseSuccess : AgendaResponseAction

    class CreateNewAgendaItem(val itemType: AgendaItemType) : AgendaResponseAction

    class OpenAgendaItemDetail(val itemId: String, val itemType: AgendaItemType, val isEditable: Boolean) :
        AgendaResponseAction

    class AgendaError(val error: DataError) : AgendaResponseAction

    data object SyncOfflineChangesSuccessful : AgendaResponseAction
}