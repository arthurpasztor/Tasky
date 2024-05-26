package com.example.tasky.main.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.auth.domain.Result
import com.example.tasky.auth.domain.RootError
import com.example.tasky.core.domain.getMillis
import com.example.tasky.main.data.ApiRepository
import com.example.tasky.main.data.dto.ReminderDTO
import com.example.tasky.main.data.dto.TaskDTO
import com.example.tasky.main.domain.AgendaItemType
import com.example.tasky.main.domain.DetailInteractionMode
import com.example.tasky.main.domain.ReminderType
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

class TaskReminderViewModel(
    private val repository: ApiRepository,
    type: AgendaItemType,
    mode: DetailInteractionMode
) : ViewModel() {

    private val _state = MutableStateFlow(TaskReminderState(agendaItemType = type, interactionMode = mode))
    val state = _state.asStateFlow()

    private val _navChannel = Channel<TaskReminderVMAction>()
    val navChannel = _navChannel.receiveAsFlow()

    fun onAction(action: TaskReminderAction) {
        when (action) {
            TaskReminderAction.OpenTitleEditor -> openTitleEditor()
            TaskReminderAction.OpenDescriptionEditor -> openDescriptionEditor()
            TaskReminderAction.SwitchToEditMode -> switchToEditMode()
            is TaskReminderAction.UpdateDate -> updateDate(action.newDate)
            is TaskReminderAction.UpdateTime -> updateTime(action.newTime)
            is TaskReminderAction.UpdateReminder -> updateReminder(action.newReminder)
            is TaskReminderAction.UpdateTitle -> updateTitle(action.newTitle)
            is TaskReminderAction.UpdateDescription -> updateDescription(action.newDescription)
            TaskReminderAction.SaveTask -> saveTask()
            TaskReminderAction.SaveReminder -> saveReminder()
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

            val payload = getTaskPayload()
            val response = if (_state.value.interactionMode == DetailInteractionMode.CREATE) {
                repository.createTask(payload)
            } else {
                repository.updateTask(payload)
            }
            _navChannel.send(TaskReminderVMAction.CreateTask(response))

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun saveReminder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val payload = getReminderPayload()
            val response = if (_state.value.interactionMode == DetailInteractionMode.CREATE) {
                repository.createReminder(payload)
            } else {
                repository.updateReminder(payload)
            }
            _navChannel.send(TaskReminderVMAction.CreateReminder(response))

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun getTaskPayload(): TaskDTO {
        _state.value.let {
            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val remindAt = it.reminderType.getReminder(time)

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

    private fun getReminderPayload(): ReminderDTO {
        _state.value.let {
            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val remindAt = it.reminderType.getReminder(time)

            return ReminderDTO(
                id = UUID.randomUUID().toString(),
                title = it.title,
                description = it.description,
                time = time.getMillis(),
                remindAt = remindAt.getMillis()
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

    val agendaItemType: AgendaItemType = AgendaItemType.TASK,
    val interactionMode: DetailInteractionMode = DetailInteractionMode.CREATE
)

sealed class TaskReminderVMAction {
    data object OpenTitleEditor : TaskReminderVMAction()
    data object OpenDescriptionEditor : TaskReminderVMAction()
    class CreateTask(val result: Result<Unit, RootError>) : TaskReminderVMAction()
    class CreateReminder(val result: Result<Unit, RootError>) : TaskReminderVMAction()
}
