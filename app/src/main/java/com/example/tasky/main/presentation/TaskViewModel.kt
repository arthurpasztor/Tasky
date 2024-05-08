package com.example.tasky.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.main.data.ApiRepository
import com.example.tasky.main.domain.DetailInteractionMode
import com.example.tasky.main.domain.ReminderType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

class TaskViewModel(private val repository: ApiRepository, mode: DetailInteractionMode) : ViewModel() {

    private val _state = MutableStateFlow(TaskState(interactionMode = mode))
    val state = _state.asStateFlow()

    private val _navChannel = Channel<TaskVMAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: TaskAction) {
        when (action) {
            TaskAction.NavigateBack -> navigateBack()
            TaskAction.SwitchToEditMode -> switchToEditMode()
            is TaskAction.UpdateDate -> updateDate(action.newDate)
            is TaskAction.UpdateTime -> updateTime(action.newTime)
            is TaskAction.UpdateReminder -> updateReminder(action.newReminder)
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navChannel.send(TaskVMAction.NavigateBack)
        }
    }

    private fun switchToEditMode() {
        viewModelScope.launch {
            _state.update { it.copy(interactionMode = DetailInteractionMode.EDIT) }
        }
    }

    private fun updateDate(date: LocalDate) {
        _state.update {
            it.copy(
                date = date
            )
        }
    }

    private fun updateTime(time: LocalTime) {
        _state.update {
            it.copy(
                time = time
            )
        }
    }

    private fun updateReminder(reminder: ReminderType) {
        _state.update {
            it.copy(
                reminderType = reminder
            )
        }
    }
}

data class TaskState(
    val isLoading: Boolean = false,

    val title: String = "Title",
    val description: String = "Description",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val reminderType: ReminderType = ReminderType.MINUTES_30,
    val isDone: Boolean = false,

    val interactionMode: DetailInteractionMode = DetailInteractionMode.CREATE
)

sealed class TaskVMAction {
    data object NavigateBack : TaskVMAction()
}