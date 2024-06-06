package com.example.tasky.main.domain

import java.time.LocalDateTime
import java.util.UUID

data class Agenda(
    val items: List<AgendaListItem> = emptyList()
) {
    companion object {
        fun getEmpty() = Agenda()

        fun getSample() = Agenda(
            listOf(
                AgendaListItem.Task.getSampleTask(),
                AgendaListItem.Reminder.getSampleReminder()
            )
        )
    }
}

sealed class AgendaListItem {

    open val id: String = ""
    open val title: String = ""
    open val description: String = ""
    open val time: LocalDateTime = LocalDateTime.now()
    open val remindAt: LocalDateTime = LocalDateTime.now()
    open val isDone: Boolean = false

    open fun getFormattedTime() : String = ""

    data class Task(
        override val id: String,
        override val title: String,
        override val description: String,
        override val time: LocalDateTime,
        override val remindAt: LocalDateTime,
        override val isDone: Boolean
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
        override val id: String,
        override val title: String,
        override val description: String,
        override val time: LocalDateTime,
        override val remindAt: LocalDateTime
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

    data object Needle : AgendaListItem()
}