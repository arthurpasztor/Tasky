package com.example.tasky.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.main.data.ApiRepository
import com.example.tasky.main.data.dto.TaskDTO
import com.example.tasky.main.domain.DetailInteractionMode
import com.example.tasky.main.domain.ReminderType
import com.example.tasky.main.domain.getMillis
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID

class TaskReminderViewModel(private val repository: ApiRepository, private val mode: DetailInteractionMode) : ViewModel() {

    private val _state = MutableStateFlow(TaskReminderState(interactionMode = mode))
    val state = _state.asStateFlow()

    private val _navChannel = Channel<TaskReminderVMAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: TaskReminderAction) {
        when (action) {
            TaskReminderAction.NavigateBack -> navigateBack()
            TaskReminderAction.OpenTitleEditor -> openTitleEditor()
            TaskReminderAction.OpenDescriptionEditor -> openDescriptionEditor()
            TaskReminderAction.SwitchToEditMode -> switchToEditMode()
            is TaskReminderAction.UpdateDate -> updateDate(action.newDate)
            is TaskReminderAction.UpdateTime -> updateTime(action.newTime)
            is TaskReminderAction.UpdateReminder -> updateReminder(action.newReminder)
            is TaskReminderAction.UpdateTitle -> updateTitle(action.newTitle)
            is TaskReminderAction.UpdateDescription -> updateDescription(action.newDescription)
            TaskReminderAction.SaveTask -> saveTask()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navChannel.send(TaskReminderVMAction.NavigateBack)
        }
    }

    private fun openTitleEditor() {
        viewModelScope.launch {
            _navChannel.send(TaskReminderVMAction.OpenTitleEditor)
        }
    }

    private fun openDescriptionEditor() {
        viewModelScope.launch {
            _navChannel.send(TaskReminderVMAction.OpenDescriptionEditor)
        }
    }

    private fun switchToEditMode() {
        _state.update { it.copy(interactionMode = DetailInteractionMode.EDIT) }
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

    private fun updateTitle(title: String) {
        _state.update {
            it.copy(
                title = title
            )
        }
    }

    private fun updateDescription(description: String) {
        _state.update {
            it.copy(
                description = description
            )
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val payload = collectTaskPayload()
            val response = if (mode == DetailInteractionMode.CREATE) {
                repository.createTask(payload)
            } else {
                repository.updateTask(payload)
            }
            _navChannel.send(TaskReminderVMAction.CreateTask(response))

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun collectTaskPayload(): TaskDTO {
        _state.value.let {
            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val remindAt: LocalDateTime = when (it.reminderType) {
                ReminderType.MINUTES_10 -> time.minusMinutes(10)
                ReminderType.MINUTES_30 -> time.minusMinutes(30)
                ReminderType.HOUR_1 -> time.minusHours(1)
                ReminderType.HOUR_6 -> time.minusHours(6)
                ReminderType.DAY_1 -> time.minusDays(1)
            }

            return TaskDTO(
                id = UUID.randomUUID().toString(),
                title = it.title,
                description = it.description,
                time = time.getMillis(),
                remindAt = remindAt.getMillis(),
                isDone = it.isDone
            )
        }
    }
}

data class TaskReminderState(
    val isLoading: Boolean = false,

    val title: String = "Title",
    val description: String = "Description",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val reminderType: ReminderType = ReminderType.MINUTES_30,
    val isDone: Boolean = false,

    val interactionMode: DetailInteractionMode = DetailInteractionMode.CREATE
)

sealed class TaskReminderVMAction {
    data object NavigateBack : TaskReminderVMAction()
    data object OpenTitleEditor : TaskReminderVMAction()
    data object OpenDescriptionEditor : TaskReminderVMAction()
    class CreateTask(val result: Result<Unit, RootError>) : TaskReminderVMAction()
}
