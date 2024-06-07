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

    open val time: LocalDateTime = LocalDateTime.now()

    open fun getFormattedTime() : String = ""

    data class TaskDM(
        val id: String = "",
        val title: String = "",
        val description: String = "",
        override val time: LocalDateTime,
        val remindAt: LocalDateTime = LocalDateTime.now(),
        val isDone: Boolean
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
        val id: String = "",
        val title: String = "",
        val description: String = "",
        override val time: LocalDateTime,
        val remindAt: LocalDateTime = LocalDateTime.now()
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