package com.example.tasky.agenda.domain.model

import com.example.tasky.agenda.domain.formatAgendaDateTime
import java.time.LocalDateTime
import java.util.UUID

sealed class AgendaListItem {

    open val time: LocalDateTime = LocalDateTime.now()

    open fun getFormattedTime() : String = ""

    data class Task(
        val id: String,
        val title: String,
        val description: String,
        override val time: LocalDateTime,
        val remindAt: LocalDateTime = LocalDateTime.now(),
        val isDone: Boolean
    ): AgendaListItem() {
        override fun getFormattedTime(): String = time.formatAgendaDateTime()

        companion object {
            fun getSampleTask() = Task(
                id = UUID.randomUUID().toString(),
                title = "Sample Task",
                description = "This is a sample task",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30),
                isDone = true
            )
        }
    }

    data class Reminder(
        val id: String,
        val title: String,
        val description: String,
        override val time: LocalDateTime,
        val remindAt: LocalDateTime = LocalDateTime.now()
    ): AgendaListItem() {
        override fun getFormattedTime(): String = time.formatAgendaDateTime()

        companion object {
            fun getSampleReminder() = Reminder(
                id = UUID.randomUUID().toString(),
                title = "Sample Reminder",
                description = "This is a sample reminder",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30)
            )
        }
    }
}