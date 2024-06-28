package com.example.tasky.agenda.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tasky.agenda.domain.AgendaItemType
import com.example.tasky.agenda.domain.EventRepository
import com.example.tasky.agenda.domain.ReminderRepository
import com.example.tasky.agenda.domain.ReminderType
import com.example.tasky.agenda.domain.TaskRepository
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.agenda.domain.model.Attendee
import com.example.tasky.agenda.domain.model.EventUpdate
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.agenda.domain.model.Photo
import com.example.tasky.auth.domain.isEmailValid
import com.example.tasky.core.data.Preferences
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
    private val eventRepo: EventRepository,
    private val taskRepo: TaskRepository,
    private val reminderRepo: ReminderRepository,
    private val prefs: Preferences,
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

        if (type == AgendaItemType.EVENT) {
            initCurrentUserFullName()
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
            is AgendaDetailAction.SaveEvent -> saveEvent(action.photoByteArrays)
            AgendaDetailAction.SaveTask -> saveTask()
            AgendaDetailAction.SaveReminder -> saveReminder()
            is AgendaDetailAction.UpdateAttendeeSelection -> updateAttendeeSelection(action.selection)
            is AgendaDetailAction.RemoveAttendee -> removeAttendee(action.userId)
            AgendaDetailAction.RemoveAgendaItem -> deleteItem()
            is AgendaDetailAction.UpdateNewAttendeeEmail -> updateNewAttendeeEmail(action.email)
            AgendaDetailAction.ClearNewAttendeeEmail -> clearNewAttendeeEmail()
            AgendaDetailAction.AddAttendee -> addAttendee()
            is AgendaDetailAction.AddNewPhoto -> addNewPhoto(action.uri)
            is AgendaDetailAction.RemovePhoto -> removePhoto(action.key)
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
                extras = updateDetailsIfEvent { eventExtras -> eventExtras.copy(toDate = date) }
            )
        }
    }

    private fun updateEventEndTime(time: LocalTime) {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras -> eventExtras.copy(toTime = time) }
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

    private fun updateAttendeeSelection(selection: AttendeeSelection) {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras -> eventExtras.copy(attendeeSelection = selection) }
            )
        }
    }

    private fun initCurrentUserFullName() {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras ->
                    val currentUserFullName = prefs.getString(Preferences.KEY_USER_NAME, "")
                    eventExtras.copy(currentUserFullName = currentUserFullName)
                }
            )
        }
    }

    private fun deleteItem() {
        state.value.itemId?.let {
            _state.update { it.copy(isLoading = true) }

            when (state.value.agendaItemType) {
                AgendaItemType.EVENT -> {
                    //TODO
                }

                AgendaItemType.TASK -> {
                    viewModelScope.launch {
                        taskRepo.deleteTask(it)
                            .onSuccess {
                                _navChannel.send(AgendaDetailVMAction.RemoveAgendaItemSuccess(AgendaItemType.TASK))
                            }
                            .onError {
                                _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                            }
                    }
                }

                AgendaItemType.REMINDER -> {
                    viewModelScope.launch {
                        reminderRepo.deleteReminder(it)
                            .onSuccess {
                                _navChannel.send(AgendaDetailVMAction.RemoveAgendaItemSuccess(AgendaItemType.REMINDER))
                            }
                            .onError {
                                _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                            }
                    }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun removeAttendee(userId: String) {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras ->
                    eventExtras.copy(
                        attendees = eventExtras.attendees.filterNot { attendee -> attendee.userId == userId }
                    )
                }
            )
        }
    }

    private fun updateNewAttendeeEmail(email: String) {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras ->
                    eventExtras.copy(
                        newAttendeeEmail = email,
                        isNewAttendeeEmailValid = email.isEmailValid(),
                        newAttendeeShouldShowEmailValidationError = !email.isEmailValid(),
                        newAttendeeShouldShowNotExistentError = false,
                        isNewAttendeeActionButtonEnabled = email.isEmailValid()
                    )
                }
            )
        }
    }

    private fun clearNewAttendeeEmail() {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras ->
                    eventExtras.copy(
                        newAttendeeEmail = "",
                        isNewAttendeeEmailValid = false,
                        newAttendeeShouldShowEmailValidationError = false,
                        newAttendeeShouldShowNotExistentError = false,
                        isNewAttendeeActionButtonEnabled = false,
                        newAttendeeJustAdded = false
                    )
                }
            )
        }
    }

    private fun addAttendee() {
        viewModelScope.launch {
            eventRepo.getAttendee(_state.value.newAttendeeEmail)
                .onSuccess { newAttendee ->
                    if (newAttendee.doesUserExist) {
                        updateWithNewAttendee(newAttendee)
                    } else {
                        showNonExistentUserError()
                    }
                }
                .onError {
                    _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                }
        }
    }

    private fun updateWithNewAttendee(newAttendee: NewAttendee) {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras ->
                    val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
                    val remindAt = it.reminderType.getReminder(time)

                    eventExtras.copy(
                        attendees = eventExtras.attendees + Attendee(
                            email = newAttendee.email!!, // the usage of non-null assertions is justified, since the user exists
                            fullName = newAttendee.fullName!!,
                            userId = newAttendee.userId!!,
                            eventId = "", // upon saving, replace with existing or generated event id
                            isGoing = true,
                            remindAt = remindAt
                        ),
                        newAttendeeJustAdded = true
                    )
                }
            )
        }
    }

    private fun showNonExistentUserError() {
        _state.update {
            it.copy(
                extras = updateDetailsIfEvent { eventExtras ->
                    eventExtras.copy(newAttendeeShouldShowNotExistentError = true)
                }
            )
        }
    }

    private fun addNewPhoto(uri: Uri?) {
        if (uri?.toString()?.isEmpty() == true) {
            viewModelScope.launch {
                _navChannel.send(AgendaDetailVMAction.PhotoUriEmptyOrNull)
            }
        } else {
            _state.update {
                it.copy(
                    extras = updateDetailsIfEvent { eventExtras ->
                        eventExtras.copy(
                            newPhotos = eventExtras.newPhotos + Photo(
                                key = UUID.randomUUID().toString(),
                                url = uri.toString()
                            )
                        )
                    }
                )
            }
        }
    }

    private fun removePhoto(key: String) {
        if (_state.value.existingPhotos.any { photo -> photo.key == key }) {
            _state.update {
                it.copy(
                    extras = updateDetailsIfEvent { eventExtras ->
                        eventExtras.copy(
                            existingPhotos = eventExtras.existingPhotos.filterNot { photo -> photo.key == key },
                            deletedPhotoKeys = eventExtras.deletedPhotoKeys + key
                        )
                    }
                )
            }
        } else if (_state.value.newPhotos.any { photo -> photo.key == key }) {
            _state.update {
                it.copy(
                    extras = updateDetailsIfEvent { eventExtras ->
                        eventExtras.copy(
                            newPhotos = eventExtras.newPhotos.filterNot { photo -> photo.key == key }
                        )
                    }
                )
            }
        }
    }

    private fun updateDetailsIfEvent(update: (AgendaItemDetails.EventItemDetail) -> AgendaItemDetails.EventItemDetail): AgendaItemDetails? {
        return when (val details = state.value.extras) {
            is AgendaItemDetails.EventItemDetail -> update(details)
            else -> details
        }
    }

    private fun saveEvent(photoByteArrays: List<ByteArray>) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when {
                _state.value.isCreateMode() -> {
                    eventRepo.createEvent(getEventPayloadForCreation(), photoByteArrays)
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.CreateAgendaItemSuccess(AgendaItemType.EVENT))
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                        }
                }

                _state.value.isEditMode() -> {
                    eventRepo.updateEvent(getEventPayloadForUpdate(), photoByteArrays)
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.UpdateAgendaItemSuccess(AgendaItemType.EVENT))
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                        }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun saveTask() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when {
                _state.value.isCreateMode() -> {
                    taskRepo.createTask(getTaskPayload())
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.CreateAgendaItemSuccess(AgendaItemType.TASK))
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                        }
                }

                _state.value.isEditMode() -> {
                    taskRepo.updateTask(getTaskPayload())
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.UpdateAgendaItemSuccess(AgendaItemType.TASK))
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
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
                            _navChannel.send(AgendaDetailVMAction.CreateAgendaItemSuccess(AgendaItemType.REMINDER))
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                        }
                }

                _state.value.isEditMode() -> {
                    reminderRepo.updateReminder(getReminderPayload())
                        .onSuccess {
                            _navChannel.send(AgendaDetailVMAction.UpdateAgendaItemSuccess(AgendaItemType.REMINDER))
                        }
                        .onError {
                            _navChannel.send(AgendaDetailVMAction.AgendaItemError(it))
                        }
                }
            }

            _state.update { it.copy(isLoading = false) }
        }
    }

    private fun loadEvent(id: String) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            eventRepo.getEventDetails(id)
                .onSuccess { event ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            itemId = event.id,
                            title = event.title,
                            description = event.description,
                            date = event.time.toLocalDate(),
                            time = event.time.toLocalTime(),
                            reminderType = ReminderType.getReminderType(event.time, event.remindAt),
                            extras = AgendaItemDetails.EventItemDetail(
                                toDate = event.to.toLocalDate(),
                                toTime = event.to.toLocalTime(),
                                isUserEventCreator = event.isUserEventCreator,
                                hostId = event.host,

                                currentUserFullName = it.currentUserFullNameIfEventCreator ?: "",

                                attendees = event.attendees.filter { attendee -> attendee.eventId == event.id },
                                nonAttendees = event.attendees.filterNot { attendee -> attendee.eventId == event.id },

                                existingPhotos = event.photos,
                            ),
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _navChannel.send(AgendaDetailVMAction.AgendaItemError(error))
                }
        }
    }

    private fun loadTask(id: String) {
        _state.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            taskRepo.getTaskDetails(id)
                .onSuccess { task ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            itemId = task.id,
                            title = task.title,
                            description = task.description,
                            date = task.time.toLocalDate(),
                            time = task.time.toLocalTime(),
                            reminderType = ReminderType.getReminderType(task.time, task.remindAt),
                            extras = AgendaItemDetails.TaskItemDetail(task.isDone),
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _navChannel.send(AgendaDetailVMAction.AgendaItemError(error))
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
                            itemId = reminder.id,
                            title = reminder.title,
                            description = reminder.description,
                            date = reminder.time.toLocalDate(),
                            time = reminder.time.toLocalTime()
                        )
                    }
                }
                .onError { error ->
                    _state.update { it.copy(isLoading = false) }
                    _navChannel.send(AgendaDetailVMAction.AgendaItemError(error))
                }
        }
    }

    private fun getEventPayloadForCreation(): Event {
        _state.value.let {
            val eventId = UUID.randomUUID().toString()

            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val endTime: LocalDateTime = LocalDateTime.of(it.eventEndDate, it.eventEndTime)
            val remindAt = it.reminderType.getReminder(time)

            it.attendees.forEach { attendee ->
                attendee.eventId = eventId
            }

            return Event(
                id = eventId,
                title = it.title,
                description = it.description,
                time = time,
                to = endTime,
                remindAt = remindAt,
                host = prefs.getEncryptedString(Preferences.KEY_USER_ID, ""),
                isUserEventCreator = true,
                attendees = it.attendees,
                photos = emptyList() // irrelevant in this context. Photos will be added in multipart form
            )
        }
    }

    private fun getEventPayloadForUpdate(): EventUpdate {
        _state.value.let {
            val from: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val to: LocalDateTime = LocalDateTime.of(it.eventEndDate, it.eventEndTime)
            val remindAt = it.reminderType.getReminder(from)

            val currentUserId = prefs.getEncryptedString(Preferences.KEY_USER_ID, "")
            val isGoing = it.attendees.find { attendee -> attendee.userId == currentUserId }?.isGoing ?: false

            return EventUpdate(
                id = it.itemId ?: UUID.randomUUID().toString(),
                title = it.title,
                description = it.description,
                from = from,
                to = to,
                remindAt = remindAt,
                attendees = it.attendees,
                deletedPhotoKeys = it.deletedPhotoKeys,
                isGoing = isGoing
            )
        }
    }

    private fun getTaskPayload(): Task {
        _state.value.let {
            val time: LocalDateTime = LocalDateTime.of(it.date, it.time)
            val remindAt = it.reminderType.getReminder(time)

            return Task(
                id = it.itemId ?: UUID.randomUUID().toString(),
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
                id = it.itemId ?: UUID.randomUUID().toString(),
                title = it.title,
                description = it.description,
                time = time,
                remindAt = remindAt
            )
        }
    }
}

sealed interface AgendaItemDetails {

    val asEventDetails: EventItemDetail?
        get() = this as? EventItemDetail

    data class TaskItemDetail(
        val isDone: Boolean = false
    ) : AgendaItemDetails

    data class EventItemDetail(
        val toDate: LocalDate = LocalDate.now(),
        val toTime: LocalTime = LocalTime.now(),
        val isUserEventCreator: Boolean = true,
        val hostId: String? = null,

        val attendeeSelection: AttendeeSelection = AttendeeSelection.ALL,

        val currentUserFullName: String = "",
        val attendees: List<Attendee> = emptyList(),
        val nonAttendees: List<Attendee> = emptyList(),

        val newAttendeeEmail: String = "",
        val isNewAttendeeEmailValid: Boolean = false,
        val newAttendeeShouldShowEmailValidationError: Boolean = false,
        val newAttendeeShouldShowNotExistentError: Boolean = false,
        val isNewAttendeeActionButtonEnabled: Boolean = false,
        val newAttendeeJustAdded: Boolean = false,

        val existingPhotos: List<Photo> = emptyList(),
        val newPhotos: List<Photo> = emptyList(),
        val deletedPhotoKeys: List<String> = emptyList()
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
    fun isEvent() = agendaItemType == AgendaItemType.EVENT

    fun isCreateMode() = itemId.isNullOrBlank()

    fun isEditMode() = !itemId.isNullOrBlank() && editable

    fun isViewMode() = !itemId.isNullOrBlank() && !editable

    val eventEndDate: LocalDate get() = extras?.asEventDetails?.toDate ?: LocalDate.now()
    val eventEndTime: LocalTime get() = extras?.asEventDetails?.toTime ?: LocalTime.now()

    val isUserEventCreator: Boolean get() = extras?.asEventDetails?.isUserEventCreator ?: true
    val hostId: String? get() = extras?.asEventDetails?.hostId

    val isAllAttendeesSelected get() = extras?.asEventDetails?.attendeeSelection == AttendeeSelection.ALL
    val isGoingAttendeesSelected get() = extras?.asEventDetails?.attendeeSelection == AttendeeSelection.GOING
    val isNotGoingAttendeesSelected get() = extras?.asEventDetails?.attendeeSelection == AttendeeSelection.NOT_GOING

    val currentUserFullNameIfEventCreator: String?
        get() = if (isUserEventCreator) extras?.asEventDetails?.currentUserFullName else null
    val attendees: List<Attendee> get() = extras?.asEventDetails?.attendees ?: emptyList()
    val nonAttendees: List<Attendee> get() = extras?.asEventDetails?.nonAttendees ?: emptyList()

    val newAttendeeEmail: String get() = extras?.asEventDetails?.newAttendeeEmail ?: ""
    val isNewAttendeeEmailValid: Boolean get() = extras?.asEventDetails?.isNewAttendeeEmailValid ?: false
    val newAttendeeShouldShowEmailValidationError: Boolean
        get() = extras?.asEventDetails?.newAttendeeShouldShowEmailValidationError ?: false
    val newAttendeeShouldShowNotExistentError: Boolean
        get() = extras?.asEventDetails?.newAttendeeShouldShowNotExistentError ?: false
    val isNewAttendeeActionButtonEnabled: Boolean
        get() = extras?.asEventDetails?.isNewAttendeeActionButtonEnabled ?: false
    val newAttendeeJustAdded: Boolean get() = extras?.asEventDetails?.newAttendeeJustAdded ?: false

    val allPhotos: List<Photo> get() {
        return mutableListOf<Photo>().apply {
            addAll(extras?.asEventDetails?.existingPhotos ?: emptyList())
            addAll(extras?.asEventDetails?.newPhotos ?: emptyList())
        }
    }
    val existingPhotos: List<Photo> get() = extras?.asEventDetails?.existingPhotos ?: emptyList()
    val newPhotos: List<Photo> get() = extras?.asEventDetails?.newPhotos ?: emptyList()
    val deletedPhotoKeys: List<String> get() = extras?.asEventDetails?.deletedPhotoKeys ?: emptyList()
}

enum class AttendeeSelection {
    ALL,
    GOING,
    NOT_GOING
}

sealed class AgendaDetailVMAction {
    data object OpenTitleEditor : AgendaDetailVMAction()
    data object OpenDescriptionEditor : AgendaDetailVMAction()
    class CreateAgendaItemSuccess(val itemType: AgendaItemType) : AgendaDetailVMAction()
    class UpdateAgendaItemSuccess(val itemType: AgendaItemType) : AgendaDetailVMAction()
    class RemoveAgendaItemSuccess(val itemType: AgendaItemType) : AgendaDetailVMAction()

    class AgendaItemError(val error: DataError) : AgendaDetailVMAction()
    data object PhotoUriEmptyOrNull : AgendaDetailVMAction()
}
