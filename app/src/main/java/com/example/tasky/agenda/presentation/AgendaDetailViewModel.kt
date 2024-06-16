package com.example.tasky.agenda.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task
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

class AgendaDetailsViewModel(
    private val taskRepo: TaskRepository,
    private val reminderRepo: ReminderRepository,
    type: AgendaItemType,
    itemId: String? = null,
    editable: Boolean = true
) : ViewModel() {

    private val _state = MutableStateFlow(
        AgendaDetailsState(
            agendaItemType = type,
            itemId = itemId,
            editable = editable,
            extras = if (type == AgendaItemType.EVENT) AgendaItemDetails.EventItemDetail() else null
        )
    )

    val state = _state.asStateFlow()

    private val _navChannel = Channel<AgendaDetailVMAction>()
    val navChannel = _navChannel.receiveAsFlow()

    init {
        // a non-null itemId means we are viewing or editing an existing item
        itemId?.let { id ->
            when (type) {
                AgendaItemType.EVENT -> loadEvent(id)
                AgendaItemType.TASK -> loadTask(id)
                AgendaItemType.REMINDER -> loadReminder(id)
            }
        }
    }

    fun onAction(action: AgendaDetailAction) {
        when (action) {
            AgendaDetailAction.OpenTitleEditor -> openTitleEditor()
            AgendaDetailAction.OpenDescriptionEditor -> openDescriptionEditor()
            AgendaDetailAction.SwitchToEditMode -> switchToEditMode()
            is AgendaDetailAction.UpdateTitle -> updateTitle(action.newTitle)
            is AgendaDetailAction.UpdateDescription -> updateDescription(action.newDescription)
            is AgendaDetailAction.UpdateDate -> updateDate(action.newDate)
            is AgendaDetailAction.UpdateTime -> updateTime(action.newTime)
            is AgendaDetailAction.UpdateEventEndDate -> updateEventEndDate(action.newDate)
            is AgendaDetailAction.UpdateEventEndTime -> updateEventEndTime(action.newTime)
            is AgendaDetailAction.UpdateReminder -> updateReminder(action.newReminder)
            AgendaDetailAction.SaveEvent -> saveEvent()
            AgendaDetailAction.SaveTask -> saveTask()
            AgendaDetailAction.SaveReminder -> saveReminder()
        }
    }

    private fun openTitleEditor() {
        viewModelScope.launch {
            _navChannel.send(AgendaDetailVMAction.OpenTitleEditor)
        }
    }

    private fun openDescriptionEditor() {
        viewModelScope.launch {
            _navChannel.send(AgendaDetailVMAction.OpenDescriptionEditor)
        }
    }

    private fun switchToEditMode() {
        _state.update {
            it.copy(
                editable = true
            )
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

    private fun updateEventEndDate(date: LocalDate) {
        _state.update {
            it.copy(
                extras = (it.extras as? AgendaItemDetails.EventItemDetail)?.copy(toDate = date)
            )
        }
    }

    private fun updateEventEndTime(time: LocalTime) {
        _state.update {
            it.copy(
                extras = (it.extras as? AgendaItemDetails.EventItemDetail)?.copy(toTime = time)
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

    private fun saveEvent() {
        //TODO
    }

    private fun saveTask() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when {
                _state.value.isCreateMode() -> {
                    taskRepo.createTask(getTaskPayload())
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.CreateTaskSuccess)
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.CreateTaskError(it))
                        }
                }

                _state.value.isEditMode() -> {
                    taskRepo.updateTask(getTaskPayload())
                        .onSuccess {
                            //TODO
                        }
                        .onError {
                            //TODO
                        }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun saveReminder() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when {
                _state.value.isCreateMode() -> {
                    reminderRepo.createReminder(getReminderPayload())
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.CreateReminderSuccess)
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.CreateReminderError(it))
                        }
                }

                _state.value.isEditMode() -> {
                    reminderRepo.updateReminder(getReminderPayload())
                        .onSuccess {
                            //TODO
                        }
                        .onError {
                            //TODO
                        }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadEvent(id: String) {
        //TODO
    }

    private fun loadTask(id: String) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            taskRepo.getTaskDetails(id)
                .onSuccess { task ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            title = task.title,
                            description = task.description,
                            date = task.time.toLocalDate(),
                            time = task.time.toLocalTime(),
                            extras = AgendaItemDetails.TaskItemDetail(task.isDone),
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _navChannel.send(AgendaDetailVMAction.LoadTaskError(error))
                }
        }
    }

    private fun loadReminder(id: String) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            reminderRepo.getReminderDetails(id)
                .onSuccess { reminder ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            title = reminder.title,
                            description = reminder.description,
                            date = reminder.time.toLocalDate(),
                            time = reminder.time.toLocalTime()
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _navChannel.send(AgendaDetailVMAction.LoadReminderError(error))
                }
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
                isDone = (it.extras as? AgendaItemDetails.TaskItemDetail)?.isDone ?: false
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

sealed interface AgendaItemDetails {
    data class TaskItemDetail(
        val isDone: Boolean = false
    ) : AgendaItemDetails

    data class EventItemDetail(
        val toDate: LocalDate = LocalDate.now(),
        val toTime: LocalTime = LocalTime.now(),
        val isUserEventCreator: Boolean = true
    ) : AgendaItemDetails
}

data class AgendaDetailsState(
    val isLoading: Boolean = false,

    val itemId: String? = null,
    val title: String = "Title",
    val description: String = "Description",
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime = LocalTime.now(),
    val reminderType: ReminderType = ReminderType.MINUTES_30,

    val agendaItemType: AgendaItemType = AgendaItemType.EVENT,
    val editable: Boolean = true,

    val extras: AgendaItemDetails? = null
) {
    fun isCreateMode() = itemId.isNullOrBlank()

    fun isEditMode() = !itemId.isNullOrBlank() && editable

    fun isViewMode() = !itemId.isNullOrBlank() && !editable

    fun getEventDate(): LocalDate {
        return (extras as? AgendaItemDetails.EventItemDetail)?.toDate ?: LocalDate.now()
    }

    fun getEventTime(): LocalTime {
        return (extras as? AgendaItemDetails.EventItemDetail)?.toTime ?: LocalTime.now()
    }
}

sealed class AgendaDetailVMAction {
    data object OpenTitleEditor : AgendaDetailVMAction()
    data object OpenDescriptionEditor : AgendaDetailVMAction()
    data object CreateTaskSuccess : AgendaDetailVMAction()
    class CreateTaskError(val error: DataError) : AgendaDetailVMAction()
    data object CreateReminderSuccess : AgendaDetailVMAction()
    class CreateReminderError(val error: DataError) : AgendaDetailVMAction()
    class LoadTaskError(val error: DataError) : AgendaDetailVMAction()
    class LoadReminderError(val error: DataError) : AgendaDetailVMAction()
}
