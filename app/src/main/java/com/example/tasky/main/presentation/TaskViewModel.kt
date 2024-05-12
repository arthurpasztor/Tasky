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

class TaskViewModel(private val repository: ApiRepository, private val mode: DetailInteractionMode) : ViewModel() {

    private val _state = MutableStateFlow(TaskState(interactionMode = mode))
    val state = _state.asStateFlow()

    private val _navChannel = Channel<TaskVMAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: TaskAction) {
        when (action) {
            TaskAction.NavigateBack -> navigateBack()
            TaskAction.OpenTitleEditor -> openTitleEditor()
            TaskAction.OpenDescriptionEditor -> openDescriptionEditor()
            TaskAction.SwitchToEditMode -> switchToEditMode()
            is TaskAction.UpdateDate -> updateDate(action.newDate)
            is TaskAction.UpdateTime -> updateTime(action.newTime)
            is TaskAction.UpdateReminder -> updateReminder(action.newReminder)
            is TaskAction.UpdateTitle -> updateTitle(action.newTitle)
            is TaskAction.UpdateDescription -> updateDescription(action.newDescription)
            TaskAction.SaveTask -> saveTask()
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _navChannel.send(TaskVMAction.NavigateBack)
        }
    }

    private fun openTitleEditor() {
        viewModelScope.launch {
            _navChannel.send(TaskVMAction.OpenTitleEditor)
        }
    }

    private fun openDescriptionEditor() {
        viewModelScope.launch {
            _navChannel.send(TaskVMAction.OpenDescriptionEditor)
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
            _navChannel.send(TaskVMAction.CreateTask(response))

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
    data object OpenTitleEditor : TaskVMAction()
    data object OpenDescriptionEditor : TaskVMAction()
    class CreateTask(val result: Result<Unit, RootError>) : TaskVMAction()
}
