package com.example.tasky.agenda.data.dto

import com.example.tasky.agenda.domain.getLocalDateTimeFromMillis
import com.example.tasky.agenda.domain.getMillis
import com.example.tasky.agenda.domain.model.Agenda
import com.example.tasky.agenda.domain.model.AgendaListItem
import com.example.tasky.agenda.domain.model.AgendaListItem.Event
import com.example.tasky.agenda.domain.model.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.model.AgendaListItem.Task
import com.example.tasky.agenda.domain.model.Attendee
import com.example.tasky.agenda.domain.model.EventUpdate
import com.example.tasky.agenda.domain.model.NewAttendee
import com.example.tasky.agenda.domain.model.Photo
import com.example.tasky.migrations.EventEntity
import com.example.tasky.migrations.ReminderEntity
import com.example.tasky.migrations.TaskEntity

fun AgendaDTO.toAgenda(): Agenda {
    val items = mutableListOf<AgendaListItem>().apply {
        addAll(events.map { it.toEvent() })
        addAll(tasks.map { it.toTask() })
        addAll(reminders.map { it.toReminder() })

        sortBy { it.time }
    }

    return Agenda(items)
}

fun TaskDTO.toTask() = Task(
    id = id,
    title = title,
    description = description,
    time = time.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis(),
    isDone = isDone
)

fun Task.toTaskDTO() = TaskDTO(
    id = id,
    title = title,
    description = description,
    time = time.getMillis(),
    remindAt = remindAt.getMillis(),
    isDone = isDone
)

fun TaskEntity.toTask() = Task(
    id = id,
    title = title,
    description = description,
    time = time.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis(),
    isDone = isDone
)

fun ReminderDTO.toReminder() = Reminder(
    id = id,
    title = title,
    description = description,
    time = time.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis()
)

fun Reminder.toReminderDTO() = ReminderDTO(
    id = id,
    title = title,
    description = description,
    time = time.getMillis(),
    remindAt = remindAt.getMillis()
)

fun ReminderEntity.toReminder() = Reminder(
    id = id,
    title = title,
    description = description,
    time = time.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis()
)

fun EventDTO.toEvent() = Event(
    id = id,
    title = title,
    description = description,
    time = from.getLocalDateTimeFromMillis(),
    to = to.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis(),
    host = host,
    isUserEventCreator = isUserEventCreator,
    attendees = attendees.map { it.toAttendee() },
    photos = photos.map { it.toPhoto() }
)

fun Event.toEventDTO() = EventDTO(
    id = id,
    title = title,
    description = description,
    from = time.getMillis(),
    to = to.getMillis(),
    remindAt = remindAt.getMillis(),
    host = host,
    isUserEventCreator = isUserEventCreator,
    attendees = attendees.map { it.toAttendeeDTO() },
    photos = photos.map { it.toPhotoDTO() }
)

fun Event.toEventCreateDTO() = EventCreateDTO(
    id = id,
    title = title,
    description = description,
    from = time.getMillis(),
    to = to.getMillis(),
    remindAt = remindAt.getMillis(),
    attendeeIds = attendees.map { it.userId }
)

fun EventUpdate.toEventUpdateDTO() = EventUpdateDTO(
    id = id,
    title = title,
    description = description,
    from = from.getMillis(),
    to = to.getMillis(),
    remindAt = remindAt.getMillis(),
    attendeeIds = attendees.map { it.userId },
    deletedPhotoKeys = deletedPhotoKeys,
    isGoing = isGoing,
)

fun EventUpdate.toEventDTO(
    host: String,
    isUserCreator: Boolean,
    photos: List<PhotoDTO>
) = EventDTO(
    id = id,
    title = title,
    description = description,
    from = from.getMillis(),
    to = to.getMillis(),
    remindAt = remindAt.getMillis(),
    host = host,
    isUserEventCreator = isUserCreator,
    attendees = attendees.map { it.toAttendeeDTO() },
    photos = photos
)

fun EventEntity.toEvent() = Event(
    id = id,
    title = title,
    description = description,
    time = startDate.getLocalDateTimeFromMillis(),
    to = endDate.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis(),
    host = host,
    isUserEventCreator = isUserEventCreator,
    attendees = attendees.map { it.toAttendee() },
    photos = photos.map { it.toPhoto() }
)

fun EventEntity.toEventUpdate(currentUserId: String): EventUpdate {
    val isGoing = attendees.find { attendee -> attendee.userId == currentUserId }?.isGoing ?: false

    return EventUpdate(
        id = id,
        title = title,
        description = description,
        from = startDate.getLocalDateTimeFromMillis(),
        to = endDate.getLocalDateTimeFromMillis(),
        remindAt = remindAt.getLocalDateTimeFromMillis(),
        attendees = attendees.map { it.toAttendee() },
        deletedPhotoKeys = deletedPhotoKeys,
        isGoing = isGoing
    )
}

fun AttendeeDTO.toAttendee() = Attendee(
    email = email,
    fullName = fullName,
    userId = userId,
    eventId = eventId,
    isGoing = isGoing,
    remindAt = remindAt.getLocalDateTimeFromMillis()
)

fun Attendee.toAttendeeDTO() = AttendeeDTO(
    email = email,
    fullName = fullName,
    userId = userId,
    eventId = eventId,
    isGoing = isGoing,
    remindAt = remindAt.getMillis()
)

fun PhotoDTO.toPhoto() = Photo(
    key = key,
    url = url
)

fun Photo.toPhotoDTO() = PhotoDTO(
    key = key,
    url = url
)

fun NewAttendeeDTO.toAttendee() = NewAttendee(
    doesUserExist = doesUserExist,
    email = attendee?.email,
    fullName = attendee?.fullName,
    userId = attendee?.userId
)
