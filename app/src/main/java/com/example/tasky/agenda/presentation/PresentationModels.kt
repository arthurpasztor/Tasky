package com.example.tasky.agenda.presentation

import com.example.tasky.agenda.domain.AgendaListItem.Reminder
import com.example.tasky.agenda.domain.AgendaListItem.Task

sealed interface AgendaItemUi {
    fun getTitle(): String = ""
    fun getDescription(): String = ""
    fun getFormattedTime(): String = ""
    fun isDone(): Boolean = false

    data class TaskUi(val task: Task): AgendaItemUi {
        override fun getTitle(): String = task.title
        override fun getDescription() = task.description
        override fun getFormattedTime() = task.getFormattedTime()
        override fun isDone() = task.isDone
    }

    data class ReminderUi(val reminder: Reminder): AgendaItemUi {
        override fun getTitle(): String = reminder.title
        override fun getDescription() = reminder.description
        override fun getFormattedTime() = reminder.getFormattedTime()
    }

    data object NeedleUi: AgendaItemUi
}
