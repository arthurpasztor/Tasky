package com.example.tasky.main.domain

import java.time.LocalDateTime
import java.util.UUID

data class AgendaDM(
    val items: List<AgendaListItem> = emptyList()
) {
    companion object {
        fun getEmpty() = AgendaDM()

        fun getSample() = AgendaDM(
            listOf(
                AgendaListItem.TaskDM.getSampleTask(),
                AgendaListItem.ReminderDM.getSampleReminder()
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

    data class TaskDM(
        override val id: String,
        override val title: String,
        override val description: String,
        override val time: LocalDateTime,
        override val remindAt: LocalDateTime,
        override val isDone: Boolean
    ): AgendaListItem() {
        override fun getFormattedTime(): String = time.formatAgendaDateTime()

        companion object {
            fun getSampleTask() = TaskDM(
                id = UUID.randomUUID().toString(),
                title = "Sample Task",
                description = "This is a sample task",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30),
                isDone = true
            )
        }
    }

    data class ReminderDM(
        override val id: String,
        override val title: String,
        override val description: String,
        override val time: LocalDateTime,
        override val remindAt: LocalDateTime
    ): AgendaListItem() {
        override fun getFormattedTime(): String = time.formatAgendaDateTime()

        companion object {
            fun getSampleReminder() = ReminderDM(
                id = UUID.randomUUID().toString(),
                title = "Sample Reminder",
                description = "This is a sample reminder",
                time = LocalDateTime.now(),
                remindAt = LocalDateTime.now().minusMinutes(30)
            )
        }
    }
}