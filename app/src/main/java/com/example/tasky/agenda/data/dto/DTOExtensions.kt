package com.example.tasky.agenda.data.dto

import com.example.tasky.agenda.domain.Agenda
import com.example.tasky.agenda.domain.AgendaListItem
import com.example.tasky.agenda.domain.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.AgendaListItem.Task
import com.example.tasky.agenda.domain.getLocalDateTimeFromMillis
import com.example.tasky.agenda.domain.getMillis

fun AgendaDTO.toAgenda(): Agenda {
    val items = mutableListOf<AgendaListItem>().apply {
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