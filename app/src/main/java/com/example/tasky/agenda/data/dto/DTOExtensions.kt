package com.example.tasky.agenda.data.dto

import com.example.tasky.agenda.domain.AgendaDM
import com.example.tasky.agenda.domain.AgendaListItem
import com.example.tasky.agenda.domain.AgendaListItem.ReminderDM
import com.example.tasky.agenda.domain.AgendaListItem.TaskDM
import com.example.tasky.agenda.domain.getLocalDateTimeFromMillis
import com.example.tasky.agenda.domain.getMillis

fun AgendaDTO.toAgenda(): AgendaDM {
    val items = mutableListOf<AgendaListItem>().apply {
        addAll(tasks.map { it.toTaskDM() })
        addAll(reminders.map { it.toReminderDM() })

        sortBy { it.time }
    }

    return AgendaDM(items)
}

fun TaskDTO.toTaskDM() = TaskDM(
    id = id,
    title = title,
    description = description,
    time = time.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis(),
    isDone = isDone
)

fun TaskDM.toTaskDTO() = TaskDTO(
    id = id,
    title = title,
    description = description,
    time = time.getMillis(),
    remindAt = remindAt.getMillis(),
    isDone = isDone
)

fun ReminderDTO.toReminderDM() = ReminderDM(
    id = id,
    title = title,
    description = description,
    time = time.getLocalDateTimeFromMillis(),
    remindAt = remindAt.getLocalDateTimeFromMillis()
)

fun ReminderDM.toReminderDTO() = ReminderDTO(
    id = id,
    title = title,
    description = description,
    time = time.getMillis(),
    remindAt = remindAt.getMillis()
)