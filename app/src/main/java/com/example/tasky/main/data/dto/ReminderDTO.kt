package com.example.tasky.main.data.dto

import com.example.tasky.main.domain.formatAgendaTimestamp
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ReminderDTO(
    val id: String,
    val title: String,
    val description: String,
    val time: Long,
    val remindAt: Long
) : AgendaListItem {

    override fun getTimestamp() = time

    override fun getItemTitle() = title

    override fun getItemDescription() = description

    override fun getFormattedTime() = time.formatAgendaTimestamp()

    companion object {
        fun getSampleReminder() = ReminderDTO(
            id = UUID.randomUUID().toString(),
            title = "Sample Reminder",
            description = "This is a sample reminder",
            time = System.currentTimeMillis(),
            remindAt = System.currentTimeMillis() + 3600000
        )
    }
}