package com.example.tasky.agenda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.agenda.domain.DetailInteractionMode
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.core.domain.DataError
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
import java.time.LocalTime
import java.util.UUID

class TaskReminderViewModel(
    private val taskRepo: TaskRepository,
    private val reminderRepo: ReminderRepository,
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

            when (_state.value.interactionMode) {
                DetailInteractionMode.CREATE -> {
                    taskRepo.createTask(getTaskPayload())
                        .onSuccess {
                            _navChannel.send(TaskReminderVMAction.CreateTaskSuccess)
                        }
                        .onError {
                            _navChannel.send(TaskReminderVMAction.CreateTaskError(it))
                        }
                }

                DetailInteractionMode.EDIT -> {
                    taskRepo.updateTask(getTaskPayload())
                        .onSuccess {
                            //TODO
                        }
                        .onError {
                            //TODO
                        }
                }

                DetailInteractionMode.VIEW -> TODO()
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun saveReminder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (_state.value.interactionMode) {
                DetailInteractionMode.CREATE -> {
                    reminderRepo.createReminder(getReminderPayload())
                        .onSuccess {
                            _navChannel.send(TaskReminderVMAction.CreateReminderSuccess)
                        }
                        .onError {
                            _navChannel.send(TaskReminderVMAction.CreateReminderError(it))
                        }
                }

                DetailInteractionMode.EDIT -> {
                    reminderRepo.updateReminder(getReminderPayload())
                        .onSuccess {
                            //TODO
                        }
                        .onError {
                            //TODO
                        }
                }

                DetailInteractionMode.VIEW -> TODO()
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun getTaskPayload(): Task {
        _state.value.let {
            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val remindAt = it.reminderType.getReminder(time)

            return Task(
                id = UUID.randomUUID().toString(),
                title = it.title,
                description = it.description,
                time = time,
                remindAt = remindAt,
                isDone = it.isDone
            )
        }
    }

    private fun getReminderPayload(): Reminder {
        _state.value.let {
            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val remindAt = it.reminderType.getReminder(time)

            return Reminder(
                id = UUID.randomUUID().toString(),
                title = it.title,
                description = it.description,
                time = time,
                remindAt = remindAt
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
    data object CreateTaskSuccess : TaskReminderVMAction()
    class CreateTaskError(val error: DataError) : TaskReminderVMAction()
    data object CreateReminderSuccess : TaskReminderVMAction()
    class CreateReminderError(val error: DataError) : TaskReminderVMAction()
}
