package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.AgendaListItem.Reminder
import com.example.tasky.main.domain.getLocalDateTimeFromMillis
import com.example.tasky.main.domain.getMillis
import kotlinx.serialization.Serializable

@Serializable
data class ReminderDTO(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
) {
    constructor(reminder: Reminder) : this(
        reminder.id,
        reminder.title,
        reminder.description,
        reminder.time.getMillis(),
        reminder.remindAt.getMillis()
    )

    fun toReminder() = Reminder(
        id = id,
        title = title,
        description = description,
        time = time.getLocalDateTimeFromMillis(),
        remindAt = remindAt.getLocalDateTimeFromMillis()
    )
}